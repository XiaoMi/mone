import html2canvas from "html2canvas"
import { jsPDF } from "jspdf";

export function html2pdf(domName, title){
	const el = document.querySelector(domName)
	return new Promise((resolve, reject) => {
		const ele = document.querySelector(domName)
		// let eleW = ele.offsetWidth;
		// let eleH = ele.offsetHeight;
		// let eleOffsetTop = ele.offsetTop;
		// let eleOffsetLeft = ele.offsetLeft;
		// let canvas = document.createElement("canvas")
		// let scaleBy = window.devicePixelRatio > 1 ? window.devicePixelRatio : 1;
		// canvas.width = eleW * 2;
		// canvas.height = eleH * 2;
		let opts = {
			width: ele.offsetWidth,
			height: ele.scrollHeight,
			useCORS: true,
			background: '#fff',
			allowTaint: false,
			windowHeight: ele.scrollHeight,
			imageTimeout: 0,
			scale: 1,
			removeContainer: true
		}

		html2canvas(el, opts).then(function(canvas) {
			let pdf = new jsPDF("p", "mm", "a4");
			let context = canvas.getContext("2d");
			let a4w = 200, a4h = 287, imgHeight = Math.floor((a4h * canvas.width) / a4w), renderHeight = 0;
			while (renderHeight < canvas.height) {
				let page = document.createElement("canvas");
				page.width = canvas.width;
				page.height = Math.min(imgHeight, canvas.height-renderHeight);
				page.getContext("2d").putImageData(
					context.getImageData(0,renderHeight,canvas.width,Math.min(imgHeight, canvas.height-renderHeight)),
					0,
					0
				);
				pdf.addImage(
					page.toDataURL("image/jpeg", 1.0),
					"JPEG",
					4,
					10,
					a4w,
					Math.min(a4h, (a4w * page.height) / page.width)
				);
				renderHeight += imgHeight;
				if (renderHeight < canvas.height) {
					pdf.addPage()
				}
			}
			try {
				pdf.save(title + ".pdf")
				resolve(true)
			} catch (error) {
				reject()
			}
		});

	})
}