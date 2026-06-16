const pictureInput = document.getElementById("picture");
const preview = document.getElementById("preview");

pictureInput.addEventListener("change", (e) => {
  const file = e.target.files[0];

  if (!file) return;

  preview.src = URL.createObjectURL(file);
});
