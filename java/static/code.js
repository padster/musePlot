lastUpdate = -1;

function pingUpdates() {
  console.log("Pinging server for updates...");
  toSend = {
    v: lastUpdate
  };
  $.getJSON('/_/ping', toSend)
    .done(data => {
      console.log("GOT! %O", data);
    })
    .fail(() => console.error("Couldn't load"));
}

function main() {
  console.log("Schedule pinging...");
  window.setInterval(pingUpdates, 1000);
}
