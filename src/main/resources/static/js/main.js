const { createApp } = Vue

createApp({
    data() {
        return {
            search: '',
            textarea: [],
        }
    },
    methods: {
        find(search) {
            // axios.get(`/api/search-list?name=${search}`)
            //     .then(res => {
            //         this.text = '';
            //         this.products = res.data;
            //         this.count = this.products.length;
            //     })
        },
        textareaAdd() {
            this.textarea.push('<span class="flex-grow-1"><textarea cols="30" rows="3" maxlength="1000"></textarea></span>');
        },
        textareaDel() {
            this.textarea.pop();
        }
    },
    created: function () {
    }
}).mount('#app')