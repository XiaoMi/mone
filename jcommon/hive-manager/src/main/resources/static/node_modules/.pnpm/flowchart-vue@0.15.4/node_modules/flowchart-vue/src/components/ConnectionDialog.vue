<template>
    <div>
        <div class="modal" v-if="visible" style="width: 320px">
            <div class="header">
                <span>Edit</span>
            </div>
            <div class="body">
                <label for="name">Name</label>
                <input id="name" class="form-control" v-model="connectionForm.name"/>
                <label for="type">Type</label>
                <select id="type" class="form-control" v-model="connectionForm.type">
                    <option :key="'connection-type-' + item.id"
                            v-for="item in [ { name: 'Pass', id: 'pass' }, { name: 'Reject', id: 'reject' } ]"
                            :value="item.id">
                        {{item.name}}
                    </option>
                </select>
            </div>
            <div class="footer">
                <button @click="handleClickCancelSaveConnection">Cancel</button>
                <button @click="handleClickSaveConnection">Ok</button>
            </div>
        </div>
    </div>
</template>
<script>
  import '../assets/modal.css';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      connection: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        connectionForm: {
          type: null,
          sourceId: null,
          sourcePosition: null,
          destinationId: null,
          destinationPosition: null,
          name: null,
          expression: null,
        },
      };
    },
    methods: {
      async handleClickSaveConnection() {
        this.$emit('update:visible', false);
        this.$emit('update:connection', Object.assign(this.connection, {
          name: this.connectionForm.name,
          type: this.connectionForm.type,
          expression: this.connectionForm.expression,
        }));
      },
      async handleClickCancelSaveConnection() {
        this.$emit('update:visible', false);
      },
    },
    watch: {
      connection: {
        immediate: true,
        handler(val) {
          if (!val) { return; }
          this.connectionForm.id = val.id;
          this.connectionForm.type = val.type;
          this.connectionForm.name = val.name;
          this.connectionForm.expression = val.expression;
        },
      },
    },
  };
</script>
