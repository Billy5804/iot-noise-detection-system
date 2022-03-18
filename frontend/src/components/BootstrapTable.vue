<template>
  <table :ref="setTable" />
</template>

<script>
import $ from "jquery";
import "bootstrap/js/dist/dropdown";
import "bootstrap-table/dist/bootstrap-table";
import "bootstrap-table/dist/extensions/sticky-header/bootstrap-table-sticky-header";
import { ref, watch, onMounted } from "vue";

export default {
  props: {
    columns: {
      type: Array,
      require: true,
    },
    data: {
      type: [Array, Object],
      default: () => undefined,
    },
    options: {
      type: Object,
      default: () => ({}),
    },
  },

  setup: function (props, context) {
    const $table = ref(null);

    function setTable(tableRef) {
      $table.value = $(tableRef);
    }

    const tableMethods = {
      ...(() => {
        const res = {};
        for (const method of $.fn.bootstrapTable.methods) {
          res[method] = function (...args) {
            return $table.value.bootstrapTable(method, ...args);
          };
        }
        return res;
      })(),
    };

    function deepCopy(arg) {
      if (arg === undefined) {
        return arg;
      }
      return $.extend(true, Array.isArray(arg) ? [] : {}, arg);
    }

    function _initTable() {
      const options = {
        ...deepCopy(props.options),
        columns: deepCopy(props.columns),
        data: deepCopy(props.data),
      };
      if (!tableMethods._hasInit) {
        $table.value.bootstrapTable(options);
        tableMethods._hasInit = true;
      } else {
        tableMethods.refreshOptions(options);
      }
    }

    onMounted(() => {
      $table.value.on("all.bs.table", (e, name, args) => {
        let eventName = $.fn.bootstrapTable.events[name];
        eventName = eventName.replace(/([A-Z])/g, "-$1").toLowerCase();
        context.emit("on-all", ...args);
        context.emit(eventName, ...args);
      });

      _initTable();
    });

    watch(
      () => props.columns,
      () => _initTable(),
      { deep: true }
    );
    watch(
      () => props.options,
      () => _initTable(),
      { deep: true }
    );
    watch(
      () => props.data,
      (value) => tableMethods.load(deepCopy(value)),
      { deep: true }
    );

    return { setTable };
  },
};
</script>
