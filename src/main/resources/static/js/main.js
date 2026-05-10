// FoodieGo - small UX helpers
(function(){
  // auto dismiss toast
  document.querySelectorAll('.toast-pop').forEach(function(el){
    setTimeout(function(){ el.style.opacity='0'; el.style.transform='translateY(20px)'; }, 3500);
    setTimeout(function(){ el.remove(); }, 4000);
  });
  // qty buttons
  document.querySelectorAll('[data-qty-btn]').forEach(function(b){
    b.addEventListener('click', function(){
      var input = document.querySelector(b.dataset.qtyTarget);
      if (!input) return;
      var v = parseInt(input.value || '1', 10);
      v += parseInt(b.dataset.qtyBtn, 10);
      if (v < 1) v = 1;
      input.value = v;
    });
  });
  // confirm delete
  document.querySelectorAll('form[data-confirm]').forEach(function(f){
    f.addEventListener('submit', function(e){
      if (!confirm(f.dataset.confirm)) e.preventDefault();
    });
  });
})();
