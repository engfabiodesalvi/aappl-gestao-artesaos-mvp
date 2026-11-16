(function () {
  var itens = Array.prototype.slice.call(document.querySelectorAll('.item'));
  var total = document.getElementById('total');
  var finalizar = document.getElementById('finalizar');
  var fmt = function (n) {
    return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  };

  function atualizar() {
    var soma = 0, qtd = 0;
    itens.forEach(function (li) {
      var campo = li.querySelector('.num');
      if (!campo) return;                       // item esgotado
      var q = +campo.value, max = +li.dataset.max;
      soma += q * parseFloat(li.dataset.preco);
      qtd += q;
      var b = li.querySelectorAll('button');
      b[0].disabled = q <= 0;                   // não desce de zero
      b[1].disabled = q >= max;                 // não passa do estoque
    });
    total.textContent = fmt(soma) + ' (' + qtd + (qtd === 1 ? ' item)' : ' itens)');
    finalizar.disabled = qtd === 0;
  }

  itens.forEach(function (li) {
    var campo = li.querySelector('.num');
    if (!campo) return;
    li.querySelectorAll('button[data-delta]').forEach(function (b) {
      b.addEventListener('click', function () {
        var novo = +campo.value + +b.dataset.delta;
        campo.value = Math.max(0, Math.min(+li.dataset.max, novo));
        atualizar();
      });
    });
  });

  document.getElementById('busca').addEventListener('input', function () {
    var termo = this.value.trim().toLowerCase();
    itens.forEach(function (li) {
      li.hidden = termo !== '' && li.dataset.busca.indexOf(termo) === -1;
    });
  });

  atualizar();
})();
