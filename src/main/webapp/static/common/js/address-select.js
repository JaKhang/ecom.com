document.addEventListener('alpine:init', () => {
    // Cấu hình API (Dùng API public trả về JSON tĩnh chuẩn)
    const API_HOST = 'https://provinces.open-api.vn';
    const API_BASE = API_HOST + '/api';

    Alpine.data('addressDropdown', (initial = {}) => ({
        provinceSearch: initial.province || "",
        selectedProvince: initial.province ? { name: initial.province, code: -1 } : null,
        districtSearch: initial.district || "",
        selectedDistrict: initial.district ? { name: initial.district, code: -1 } : null,
        wardSearch: initial.ward || "",
        selectedWard: initial.ward ? { name: initial.ward, code: -1 } : null,
        allProvinces: [],
        allDistricts: [],
        allWards: [],
        filteredProvinces: [],
        filteredDistricts: [],
        provinceListShown: false,
        districtListShown: false,
        wardListShown: false,

        async init() {
            // 1. Tải toàn bộ danh sách Tỉnh ngay khi vào trang
            await this.fetchProvinces();

            // 2. Lắng nghe sự thay đổi để lọc (Client-side)
            this.$watch("provinceSearch", () => this.filterLocal('province'));
            this.$watch("districtSearch", () => this.filterLocal('district'));
            this.$watch("wardSearch", () => this.filterLocal('ward'));
        },

        // --- FETCH DATA TỪ API ---

        async fetchProvinces() {
            try {
                // Gọi API lấy tất cả tỉnh thành
                const res = await fetch(`${API_BASE}/p/`);
                const data = await res.json();
                this.allProvinces = data;
                this.filteredProvinces = data; // Ban đầu hiển thị hết
            } catch (e) {
                console.error("Lỗi tải tỉnh:", e);
            }
        },

        async fetchDistricts(provinceCode) {
            try {
                // Gọi API lấy chi tiết Tỉnh (bao gồm danh sách Huyện)
                const res = await fetch(`${API_BASE}/p/${provinceCode}?depth=2`);
                const data = await res.json();
                this.allDistricts = data.districts || [];
                this.filteredDistricts = this.allDistricts;
            } catch (e) {
                console.error("Lỗi tải huyện:", e);
            }
        },

        async fetchWards(districtCode) {
            try {
                // Gọi API lấy chi tiết Huyện (bao gồm danh sách Xã)
                const res = await fetch(`${API_BASE}/d/${districtCode}?depth=2`);
                const data = await res.json();
                this.allWards = data.wards || [];
                this.filteredWards = this.allWards;
            } catch (e) {
                console.error("Lỗi tải xã:", e);
            }
        },

        // --- LOGIC LỌC (FILTER) TẠI TRÌNH DUYỆT ---

        filterLocal(type) {
            const removeAccents = (str) => {
                return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
            };

            if (type === 'province') {
                this.provinceListShown = true;
                const term = removeAccents(this.provinceSearch);
                this.filteredProvinces = this.allProvinces.filter(item =>
                    removeAccents(item.name).includes(term)
                );
            } else if (type === 'district') {
                this.districtListShown = true;
                const term = removeAccents(this.districtSearch);
                this.filteredDistricts = this.allDistricts.filter(item =>
                    removeAccents(item.name).includes(term)
                );
            } else if (type === 'ward') {
                this.wardListShown = true;
                const term = removeAccents(this.wardSearch);
                this.filteredWards = this.allWards.filter(item =>
                    removeAccents(item.name).includes(term)
                );
            }
        },

        // --- EVENT HANDLERS (FOCUS & SELECT) ---

        startSearchingProvince() {
            this.provinceListShown = true;
            // Nếu ô tìm kiếm trống, hiển thị lại toàn bộ
            if (!this.provinceSearch) this.filteredProvinces = this.allProvinces;
        },

        selectProvince(item) {
            this.selectedProvince = item;
            this.provinceSearch = item.name;
            this.provinceListShown = false;

            // Reset cấp dưới
            this.resetDistrict();
            this.resetWard();

            // Tải dữ liệu Huyện mới
            this.fetchDistricts(item.code);
        },

        startSearchingDistrict() {
            if (this.selectedProvince) {
                this.districtListShown = true;
                if (!this.districtSearch) this.filteredDistricts = this.allDistricts;
            }
        },

        searchDistrictOnTyping() {
            // Hàm này để giữ tương thích với HTML cũ, logic đã xử lý ở $watch
        },

        selectDistrict(item) {
            this.selectedDistrict = item;
            this.districtSearch = item.name;
            this.districtListShown = false;

            // Reset cấp dưới
            this.resetWard();

            // Tải dữ liệu Xã mới
            this.fetchWards(item.code);
        },

        startSearchingWard() {
            if (this.selectedDistrict) {
                this.wardListShown = true;
                if (!this.wardSearch) this.filteredWards = this.allWards;
            }
        },

        selectWard(item) {
            this.selectedWard = item;
            this.wardSearch = item.name;
            this.wardListShown = false;
        },

        // --- HELPERS ---

        resetDistrict() {
            this.districtSearch = "";
            this.selectedDistrict = null;
            this.allDistricts = [];
            this.filteredDistricts = [];
            this.districtListShown = false;
        },

        resetWard() {
            this.wardSearch = "";
            this.selectedWard = null;
            this.allWards = [];
            this.filteredWards = [];
            this.wardListShown = false;
        },

        // Hàm highlight đơn giản (Tô đậm từ khóa khớp)
        highlightName(item) {
            const name = item.name;
            // Xác định từ khóa đang tìm kiếm dựa trên item thuộc loại nào
            let term = '';
            if (this.allProvinces.includes(item)) term = this.provinceSearch;
            else if (this.allDistricts.includes(item)) term = this.districtSearch;
            else if (this.allWards.includes(item)) term = this.wardSearch;

            if (!term || term.trim() === '') return name;

            // Tạo Regex tìm kiếm không phân biệt hoa thường
            try {
                const regex = new RegExp(`(${term})`, 'gi');
                return name.replace(regex, '<strong>$1</strong>');
            } catch (e) {
                return name;
            }
        }
    }));
});
