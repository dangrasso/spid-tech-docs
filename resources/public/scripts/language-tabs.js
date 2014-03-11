/*global cull, bane, dom */

(function () {
  var c = cull;

  var eventHub = bane.createEventEmitter();

  function setupTabContent(tabs) {
    var container = document.createElement("div");
    var selectedTab;

    function selectTab(tab) {
      if (selectedTab) { selectedTab.className = "tab"; }
      tab.className = "tab active";
      container.innerHTML = dom.nextSibling(tab).innerHTML;
      selectedTab = tab;
    }

    c.doall(function (node) {
      if (node.className === "tab") {
        eventHub.on("language:" + node.innerHTML, function () {
          selectTab(node);
        });
      }
    }, tabs.childNodes);

    container.className = "tabs-content";
    selectTab(dom.firstChild(tabs));
    dom.insertAfter(container, tabs);
  }

  function setupTab(tab) {
    tab.onclick = function () {
      eventHub.emit("language:" + tab.innerHTML);
      document.cookie = "lang=" + tab.innerHTML;
    };
  }

  c.doall(setupTab, document.querySelectorAll(".tab"));
  c.doall(setupTabContent, document.querySelectorAll("div.tabs"));

  function cookieValue(key) {
    return document.cookie.replace(new RegExp("(?:(?:^|.*;\s*)" + key + "\s*\=\s*([^;]*).*$)|^.*$"), "$1");
  }

  if (cookieValue("lang")) {
    eventHub.emit("language:" + cookieValue("lang"));
  }


}());
