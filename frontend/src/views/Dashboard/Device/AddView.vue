<script>
import { ref, shallowRef } from "vue";
import { useUserStore } from "@/stores/UserStore";
import axios from "axios";
import { MDBRow, MDBCol } from "mdb-vue-ui-kit";
import AjaxButton from "@/components/AjaxButton.vue";
import FormInput from "@/components/FormInput.vue";
import SensorUnits from "@/utilitys/SensorUnits";

export default {
  components: {
    MDBRow,
    MDBCol,
    AjaxButton,
    FormInput,
  },

  emits: ["done"],

  props: {
    siteId: {
      type: String,
      required: true,
    },
    siteDevices: {
      type: Object,
      required: true,
    },
  },

  setup: function (props, context) {
    const { getIdToken } = useUserStore();

    const formChecked = ref(false);
    const syncing = ref(false);

    const addError = shallowRef(null);

    const deviceId = ref("");
    const deviceIdValidity = shallowRef({});

    const displayName = ref("");
    const displayNameValidity = shallowRef({});

    async function submitAddForm() {
      addError.value = null;
      if (!displayNameValidity.value.valid || !deviceIdValidity.value.valid) {
        formChecked.value = true;
        return;
      }
      syncing.value = true;
      axios
        .post(
          `${
            import.meta.env.BASE_URL
              ? import.meta.env.BASE_URL
              : "http://localhost:443"
          }/api/v1/site-devices`,
          {
            deviceId: deviceId.value,
            displayName: displayName.value,
            siteId: props.siteId,
          },
          {
            timeout: 5000,
            headers: { authorization: await getIdToken() },
          }
        )
        .then(({ data }) => {
          const { id: deviceId, sensors, ...device } = data;
          device.sensors = sensors.map(({ unit, ...sensor }) => ({
            ...sensor,
            unit: SensorUnits[unit],
          }));
          Object.assign(props.siteDevices, { [deviceId]: device });
          context.emit("done");
        })
        .catch((error) => (addError.value = error.message || error))
        .finally(() => (syncing.value = false));
    }

    return {
      formChecked,
      syncing,
      addError,
      deviceId,
      deviceIdValidity,
      displayName,
      displayNameValidity,
      submitAddForm,
    };
  },
};
</script>

<template>
  <MDBRow
    tag="form"
    class="g-3 mb-3 needs-validation"
    :class="formChecked && 'was-validated'"
    novalidate
    @submit.prevent="submitAddForm"
  >
    <FormInput
      type="text"
      size="lg"
      v-model.trim="deviceId"
      label="Device Code"
      invalidFeedback="Please provide your devices code"
      @update:validity="deviceIdValidity = $event"
      required
      :formChecked="formChecked"
      :pattern="'[0-9A-Fa-f]{12}'"
      :minlength="12"
      :maxlength="12"
      placeholder="E320D8F1F35F"
    />
    <FormInput
      type="text"
      size="lg"
      v-model.trim="displayName"
      label="Display Name"
      invalidFeedback="Please provide a display name"
      @update:validity="displayNameValidity = $event"
      required
      :formChecked="formChecked"
      counter
      :maxlength="32"
      class="mb-3"
    />
    <MDBCol md="12" v-if="addError">
      <div class="alert alert-danger text-center p-2 mb-0" role="alert">
        {{ addError }}
      </div>
    </MDBCol>
    <MDBCol md="12">
      <AjaxButton
        color="success"
        type="submit"
        size="lg"
        class="w-100"
        :syncing="syncing"
        >Add Device</AjaxButton
      >
    </MDBCol>
  </MDBRow>
</template>
