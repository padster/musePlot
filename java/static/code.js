updateAfter = 0; // 0 = first request.

function handleNoData() {
  console.log('No data!');
}

function handleUpdate(data) {
  data = data || []
  if (data.length == 0) {
    handleNoData();
  } else {
    console.log("RECV>");
    for (bundle of data) {
      console.log(bundle);
    }
    updateAfter = data[data.length - 1].timestamp;
  }
}

function pingUpdates() {
  console.log("Pinging server for updates...");
  toSend = {
    msAt: updateAfter,
  };
  $.getJSON('/_/ping', toSend)
    .done(data => handleUpdate(data))
    .fail(() => console.error("Couldn't load"));
}

function main() {
  console.log("Schedule pinging...");
  window.setInterval(pingUpdates, 1000);
}
