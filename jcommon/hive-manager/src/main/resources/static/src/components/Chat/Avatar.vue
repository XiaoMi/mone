<template>
  <div class="avatar-box" :class="{ 'user-avatar': isUser }">
    <div
      class="vue-avatar--wrapper"
      :style="[style, customStyle]"
      aria-hidden="true"
    >
      <img
        v-if="isImage"
        style="display: none"
        :src="src"
        @error="onImgError"
      />
      <ErrorAvatar v-else-if="isSystem" />
      <!-- <SuccessAvatar v-else-if="isSystemSuccess" /> -->
      <span v-else>{{ userInitial }}</span>
    </div>
    <span class="full-name">{{ username }}</span>
  </div>
</template>

<script lang="ts">
import ErrorAvatar from "./components/icons/IconErrorAvatar.vue";
// import SuccessAvatar from "./components/icons/IconSuccessAvatar.vue";

const getInitials = (username: string) => {
  let parts = username.split(/[ -]/);
  let initials = "";

  for (var i = 0; i < parts.length; i++) {
    initials += parts[i].charAt(0);
  }

  if (initials.length > 3 && initials.search(/[A-Z]/) !== -1) {
    initials = initials.replace(/[a-z]+/g, "");
  }

  initials = initials.substr(0, 3).toUpperCase();

  return initials;
};

export default {
  name: "avatar",
  props: {
    username: {
      type: String,
    },
    initials: {
      type: String,
    },
    backgroundColor: {
      type: String,
    },
    color: {
      type: String,
    },
    customStyle: {
      type: Object,
    },
    inline: {
      type: Boolean,
    },
    size: {
      type: Number,
      default: 30,
    },
    src: {
      type: String,
    },
    rounded: {
      type: Boolean,
      default: true,
    },
    lighten: {
      type: Number,
      default: 80,
    },
    parser: {
      type: Function,
      default: getInitials,
      validator: (parser) => typeof parser("John", getInitials) === "string",
    },
    isUser: {
      type: Boolean,
      default: false,
    },
  },
  components: {
    ErrorAvatar,
    // SuccessAvatar,
  },
  data() {
    return {
      backgroundColors: [
        "#F44336",
        "#FF4081",
        "#9C27B0",
        "#673AB7",
        "#3F51B5",
        "#2196F3",
        "#03A9F4",
        "#00BCD4",
        "#009688",
        "#4CAF50",
        "#8BC34A",
        "#CDDC39",
        /* '#FFEB3B' , */ "#FFC107",
        "#FF9800",
        "#FF5722",
        "#795548",
        "#9E9E9E",
        "#607D8B",
      ],
      imgError: false,
    };
  },

  mounted() {
    if (!this.isImage) {
      this.$emit("avatar-initials", this.username, this.userInitial);
    }
  },

  computed: {
    background() {
      if (!this.isImage) {
        return (
          this.backgroundColor ||
          this.randomBackgroundColor(
            this.username.length,
            this.backgroundColors
          )
        );
      }
    },

    fontColor() {
      if (!this.isImage) {
        return this.color || this.lightenColor(this.background, this.lighten);
      }
    },

    isImage() {
      return !this.imgError && Boolean(this.src);
    },

    isSystem() {
      return this.username === "error";
    },
    // isSystemSuccess() {
    //   return this.username === "success";
    // },
    style() {
      const style = {
        display: this.inline ? "inline-flex" : "flex",
        width: `${this.size}px`,
        height: `${this.size}px`,
        borderRadius: this.rounded ? "50%" : 0,
        lineHeight: `${this.size + Math.floor(this.size / 20)}px`,
        fontWeight: "bold",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        userSelect: "none",
      };

      const imgBackgroundAndFontStyle = {
        background: `#fff url('${this.src}') no-repeat scroll 0% 0% / ${this.size}px ${this.size}px content-box border-box`,
      };

      const initialBackgroundAndFontStyle = {
        backgroundColor: this.background,
        font: `${Math.floor(this.size / 2.5)}px/${
          this.size
        }px Helvetica, Arial, sans-serif`,
        color: this.fontColor,
      };

      const backgroundAndFontStyle =
        this.isImage || this.isSystem
          ? imgBackgroundAndFontStyle
          : initialBackgroundAndFontStyle;

      Object.assign(style, backgroundAndFontStyle);

      return style;
    },

    userInitial() {
      if (!this.isImage) {
        const initials =
          this.initials || this.parser(this.username, getInitials);
        return initials;
      }
      return "";
    },
  },

  methods: {
    initial: getInitials,

    onImgError(evt: any) {
      this.imgError = true;
    },

    randomBackgroundColor(seed: number, colors: string | any[]) {
      return colors[seed % colors.length];
    },

    lightenColor(hex: string | any[], amt: number) {
      // From https://css-tricks.com/snippets/javascript/lighten-darken-color/
      var usePound = false;

      if (hex[0] === "#") {
        hex = hex.slice(1);
        usePound = true;
      }

      var num = parseInt(hex, 16);
      var r = (num >> 16) + amt;

      if (r > 255) r = 255;
      else if (r < 0) r = 0;

      var b = ((num >> 8) & 0x00ff) + amt;

      if (b > 255) b = 255;
      else if (b < 0) b = 0;

      var g = (num & 0x0000ff) + amt;

      if (g > 255) g = 255;
      else if (g < 0) g = 0;

      return (usePound ? "#" : "") + (g | (b << 8) | (r << 16)).toString(16);
    },
  },
};
</script>
<style scoped>
.avatar-box {
  display: flex;
  color: #fff;
  align-items: center;
  margin-bottom: 10px;
}
.user-avatar {
  flex-direction: row-reverse;
  margin-right: 0 !important;
}
.full-name {
  margin-left: 10px;
  margin-right: 10px;
  font-weight: 500;
}
</style>
