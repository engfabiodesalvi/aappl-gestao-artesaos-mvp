document.getElementById('ver').addEventListener('click', function () {
  var campo = document.getElementById('password');
  var oculto = campo.type === 'password';
  campo.type = oculto ? 'text' : 'password';
  this.textContent = oculto ? 'Ocultar' : 'Ver';
  this.setAttribute('aria-label', oculto ? 'Ocultar senha' : 'Mostrar senha');
});
