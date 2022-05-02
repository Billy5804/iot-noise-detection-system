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
    locationDevices: Object,
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

    function drawDevices(canvas, scale, width, height) {
      const fontSize = 25 * scale;

      Object.values(deviceIcons).forEach((deviceIcon) =>
        canvas.remove(deviceIcon)
      );
      canvas.renderAll();

      Object.entries(props.locationDevices || {}).forEach(
        ([deviceId, { type, positionX, positionY }]) => {
          positionX = positionX < 0 ? 0 : positionX > width ? width : positionX;
          positionY =
            positionY < 0 ? 0 : positionY > height ? height : positionY;

          deviceIcons[deviceId] = new fabric.Text(type.getUnicodeIcon(), {
            deviceId,
            type: deviceIconType,
            left: positionX,
            top: positionY,
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
      canvas.renderAll();
    }

    function scaleCanvas(canvas, width, height, ratio) {
      canvas.setDimensions({ width: width * ratio, height: height * ratio });
      canvas.setZoom(ratio);
      canvas.renderAll();
    }

    onMounted(async () => {
      const floorPlan = await new Promise((resolve) =>
        fabric.Image.fromURL(props.floorPlanURL, resolve)
      );

      loading.value = false;

      const canvasParent = canvasRef.value.parentElement;
      const canvas = new fabric.Canvas(canvasRef.value);

      const width = floorPlan.width;
      const height = floorPlan.height;

      canvas.setWidth(width);
      canvas.setHeight(height);

      const objectScale = width / 833;

      drawDevices(canvas, objectScale, width, height);

      watch(
        () => Object.keys(props.locationDevices || {}),
        () => {
          drawDevices(canvas, objectScale, width, height);
          canvas.renderAll();
        }
      );

      const tooltipBackground = new fabric.Rect({
        fill: "#ffffff",
        rx: 8 * objectScale,
        ry: 8 * objectScale,
        originX: "center",
        originY: "center",
        width: 110 * objectScale,
        height: 110 * objectScale,
        shadow: {
          offsetY: 8 * objectScale,
          blur: 15 * objectScale,
          color: "rgb(0 0 0 / 25%)",
        },
      });

      const tooltipText = new fabric.Textbox("", {
        fontFamily: "'Roboto', sans-serif",
        fontSize: 15 * objectScale,
        width: 100 * objectScale,
        textAlign: "center",
        splitByGrapheme: true,
        editable: false,
        strokeWidth: 0,
        originX: "center",
        originY: "center",
        fill: "#000000",
      });

      const tooltip = new fabric.Group([tooltipBackground, tooltipText], {
        width: 110 * objectScale,
        height: 110 * objectScale,
        selectable: false,
        hoverCursor: "normal",
        originX: "center",
        originY: "center",
      });

      canvas.setBackgroundImage(floorPlan, canvas.renderAll.bind(canvas), {});

      scaleCanvas(canvas, width, height, canvasParent.clientWidth / width);

      canvas.on("mouse:over", function ({ target }) {
        if (
          target?.type === deviceIconType &&
          target.deviceId !== props.selectedDeviceId
        ) {
          target.fontSize = target.originalFontSize * 1.5;
          tooltipText.text = props.locationDevices[target.deviceId].displayName;
          console.log(target.left);
          console.log(target.top);
          tooltip.left = target.left;
          tooltip.top =
            target.top - tooltipBackground.height / 2 - target.height;
          canvas.add(tooltip);
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
        canvas.remove(tooltip);
        if (
          target?.type === deviceIconType &&
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

      new ResizeObserver(() =>
        scaleCanvas(canvas, width, height, canvasParent.clientWidth / width)
      ).observe(canvasParent);
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
