<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
</main><!-- /#mainContent -->

<!-- Synchronizer-token CSRF: attach a server-generated token to every HTML form submission. -->
<script>
(function () {
  var token = '${requestScope.eclatCsrfToken}';
  if (!token) return;
  document.querySelectorAll('form').forEach(function (form) {
    if ((form.method || 'get').toLowerCase() !== 'post') return;
    if (form.querySelector('input[name="_csrf"]')) return;
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = '_csrf';
    input.value = token;
    form.appendChild(input);
  });
})();
</script>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
