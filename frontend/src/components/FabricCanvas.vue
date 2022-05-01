<template>
  <div :class="loading ? 'position-relative h-100' : ''">
    <LoadingView v-if="loading" />
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script>
import { fabric } from "fabric";
import { ref, onMounted, watch } from "vue";
import LoadingView from "@/views/LoadingView.vue";

export default {
  props: {
    floorPlanURL: { type: String, required: true },
    locationDevices: { type: Object, required: true },
    selectedDeviceId: String,
  },

  emits: ["update:selectedDeviceId"],

  components: { LoadingView },

  setup: function (props, { emit }) {
    const loading = ref(true);
    const canvasRef = ref(null);
    const deviceIconType = "deviceIcon";
    const deviceIcons = {};

    fabric.Text.prototype.set({
      fill: "#000000",
      stroke: "#ffffff",
      strokeWidth: 5,
      paintFirst: "stroke",
    });

    function addDevicesToCanvas(canvas) {
      const width = canvas.getWidth();
      const height = canvas.getHeight();
      const fontSize = 25 * (width / 833);
      Object.entries(props.locationDevices).forEach(
        ([deviceId, { type, positionX, positionY }]) => {
          positionX = positionX < 0 ? 0 : positionX > width ? width : positionX;
          positionY =
            positionY < 0 ? 0 : positionY > height ? height : positionY;

          deviceIcons[deviceId] = new fabric.Text(type.getUnicodeIcon(), {
            deviceId,
            type: deviceIconType,
            left: positionX,
            top: height - positionY,
            originX: "center",
            originY: "center",
            fontSize,
            originalFontSize: fontSize,
            fontFamily: "FontAwesome",
            selectable: false,
            editable: false,
            hoverCursor: "pointer",
            _controlsVisibility: {
              mt: false,
              mb: false,
              ml: false,
              mr: false,
              bl: false,
              br: false,
              tl: false,
              tr: false,
              mtr: false,
            },
          });
          canvas.add(deviceIcons[deviceId]);
        }
      );
    }

    function scaleCanvas(canvas, width, height, ratio) {
      canvas.setDimensions({ width: width * ratio, height: height * ratio });
      canvas.setZoom(ratio);
      canvas.renderAll();
    }

    onMounted(async () => {
      const floorPlan = await new Promise((resolve) =>
        fabric.Image.fromURL(props.floorPlanURL, (floorPlan) =>
          resolve(floorPlan)
        )
      );

      loading.value = false;

      const canvasParent = canvasRef.value.parentElement;
      const canvas = new fabric.Canvas(canvasRef.value);

      canvas.setWidth(floorPlan.width);
      canvas.setHeight(floorPlan.height);

      addDevicesToCanvas(canvas);

      canvas.setBackgroundImage(floorPlan, canvas.renderAll.bind(canvas), {});

      scaleCanvas(
        canvas,
        floorPlan.width,
        floorPlan.height,
        canvasParent.clientWidth / floorPlan.width
      );

      canvas.on("mouse:over", function ({ target }) {
        if (
          target?.type === deviceIconType &&
          target.deviceId !== props.selectedDeviceId
        ) {
          target.fontSize = target.originalFontSize * 1.5;
          canvas.renderAll();
        }
      });

      canvas.on("mouse:up", function ({ target }) {
        if (target?.type === deviceIconType) {
          const deviceId = target.deviceId;
          emit(
            "update:selectedDeviceId",
            deviceId === props.selectedDeviceId ? null : deviceId
          );
        }
      });

      canvas.on("mouse:out", function ({ target }) {
        if (
          target.type === deviceIconType &&
          target.deviceId !== props.selectedDeviceId
        ) {
          target.fontSize = target.originalFontSize;
          canvas.renderAll();
        }
      });

      watch(
        () => props.selectedDeviceId,
        (selectedDeviceId) => {
          Object.entries(deviceIcons).forEach(([deviceId, deviceIcon]) => {
            deviceIcon.fontSize =
              selectedDeviceId === deviceId
                ? deviceIcon.originalFontSize * 2
                : deviceIcon.originalFontSize;
            deviceIcon.fill =
              selectedDeviceId === deviceId ? "#1266f1" : "#000000";
          });
          canvas.renderAll();
        }
      );

      new ResizeObserver(() => {
        scaleCanvas(
          canvas,
          floorPlan.width,
          floorPlan.height,
          canvasParent.clientWidth / floorPlan.width
        );
      }).observe(canvasParent);
    });

    return { loading, canvasRef };
  },
};
</script>

<style>
.canvas-container {
  width: 100% !important;
  height: 75vh;
  max-height: 80vh;
}
</style>
