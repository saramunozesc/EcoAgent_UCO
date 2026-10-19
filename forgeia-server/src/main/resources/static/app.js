(function () {
  var buttons = document.querySelectorAll("[data-screen], [data-screen-link]");
  var screens = document.querySelectorAll(".screen");
  var navItems = document.querySelectorAll(".nav-item");

  /**
   * Shows one screen and updates the active navigation item.
   */
  function showScreen(id) {
    screens.forEach(function (screen) {
      screen.classList.toggle("active", screen.id === id);
    });
    navItems.forEach(function (item) {
      item.classList.toggle("active", item.getAttribute("data-screen") === id);
    });
  }

  buttons.forEach(function (button) {
    button.addEventListener("click", function () {
      var target = button.getAttribute("data-screen") || button.getAttribute("data-screen-link");
      if (target) {
        showScreen(target);
      }
    });
  });

  var engineStatus = document.getElementById("engine-status");
  if (engineStatus && window.fetch) {
    window.fetch("/api/health", { headers: { "Accept": "application/json" } })
      .then(function (response) {
        if (!response.ok) {
          throw new Error("HTTP " + response.status);
        }
        return response.json();
      })
      .then(function (health) {
        engineStatus.textContent = health.status === "UP" ? "Disponible" : "No disponible";
      })
      .catch(function () {
        engineStatus.textContent = "No disponible";
      });
  }
})();
