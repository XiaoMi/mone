import Img1 from "@/assets/img1.png";
import Img2 from "@/assets/img2.png";
import Img3 from "@/assets/img3.png";
import Img4 from "@/assets/img4.png";
import Img6 from "@/assets/img6.png";
import Img7 from "@/assets/img7.png";
import Img8 from "@/assets/img8.png";
import Person from "@/assets/person.png";

export const MALE = [Img8, Img4, Img7];
export const FE_MALE = [Img3, Img6, Img1, Img2];
export const fontColor = [
  "#5897ff",
  "#ff7d25",
  "#ff25f6",
  "#d64a4a",
  "#b117ed",
];

export const USER_ROLE = {
  USER: {
    role: "USER",
    img: Img4,
    fontColor: "#2c3e50",
    name: "张三",
  },
  BOT: {
    role: "BOT",
    img: Img2,
    fontColor: "#5897ff",
    name: "李四",
  },
  ASSISTANT: {
    role: "ASSISTANT",
    img: Img3,
    fontColor: "#d5fcbd",
    name: "小红",
  },
  PERSON: {
    role: "PERSON",
    img: Person,
    fontColor: "#2c3e50",
    name: "",
  },
};
