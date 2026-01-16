<template>
    <div>
        <div class="modal" v-if="visible" style="width: 320px">
            <div class="header">
                <span>Edit</span>
            </div>
            <div class="body">
                <label for="name">Name</label>
                <input class="form-control" id="name" v-model="nodeForm.name"/>
                <label for="type">Type</label>
                <select class="form-control" id="type" v-model="nodeForm.type">
                    <option :key="'node-type-' + item.id" :value="item.id"
                            v-for="item in [ { name: 'Start', id: 'start' }, { name: 'End', id: 'end' }, { name: 'Operation', id: 'operation' } ]"
                    >
                        {{item.name}}
                    </option>
                </select>
                <label for="approver">Approver</label>
                <select class="form-control" id="approver" :value="nodeForm.approver.id"
                        @change="handleChangeApprover($event)">
                    <option :value="item.id" :key="'approver-' + item.id" v-for="item in approvers">
                        {{item.name}}
                    </option>
                </select>
            </div>
            <div class="footer">
                <button @click="handleClickCancelSaveNode">Cancel</button>
                <button @click="handleClickSaveNode">Ok</button>
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
      node: {
        type: Object,
        default: null,
      },
    },
    data: function() {
      return {
        nodeForm: {name: null, id: null, type: null, approver: []},
        approvers: [{id: 1, name: 'Joyce'}, {id: 2, name: 'Allen'}, {id: 3, name: 'Teresa'}],
      };
    },
    methods: {
      handleClickSaveNode() {
        this.$emit('update:node', Object.assign(this.node, {
          name: this.nodeForm.name,
          type: this.nodeForm.type,
          approvers: [Object.assign({}, this.nodeForm.approver)],
        }));
        this.$emit('update:visible', false);
      },
      handleClickCancelSaveNode() {
        this.$emit('update:visible', false);
      },
      handleChangeApprover(e) {
        this.nodeForm.approver = this.approvers.filter(i => i.id === parseInt(e.target.value))[0];
      },
    },
    watch: {
      node: {
        immediate: true,
        handler(val) {
          if (!val) { return; }
          this.nodeForm.id = val.id;
          this.nodeForm.name = val.name;
          this.nodeForm.type = val.type;
          if (val.approvers && val.approvers.length > 0) {
            this.nodeForm.approver = val.approvers[0];
          }
        },
      },
    },
  };
</script>