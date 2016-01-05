document.addEventListener('DOMContentLoaded', function () {

    startup();

    var defaultOpt = 0;
    var motorOpt = 1;
    var distanceOpt = 2;
    var speedOpt = 3;
    var cameraOpt = 4;
    var servoOpt = 5;
    var compassOpt = 6;
    var licenseOpt = 7;
    var wallStopOpt = 8;
    var vibrateOpt = 9;

    var socket = new WebSocket("ws://pi.akoo.nl:1212");
    var sendBuffer = new Uint8Array(new ArrayBuffer(2));

    socket.onopen = function () {
        console.log('Socket Status: ' + socket.readyState + ' (open)');

        var button = document.querySelector('button');
        button.addEventListener('click', function () {
            sendBuffer.set([licenseOpt, 0]);
            socket.send(sendBuffer);
        }, false);

        var checkbox = document.querySelector('.stop input');
        checkbox.addEventListener('change', function () {

            if (checkbox.checked) {
                sendBuffer.set([wallStopOpt, true]);
            } else {
                sendBuffer.set([wallStopOpt, false]);
            }

            socket.send(sendBuffer);
        }, false);
    };

    socket.onerror = function (error) {
        console.log(error);
    };

    socket.onmessage = function (msg) {
//                console.log('Received: ' + msg.data);
        var opt = parseInt(msg.data[0]);
        var pld = msg.data.substring(1, 1024);
        switch (opt) {
            case compassOpt:
                document.querySelector('.compass img').style.transform = "rotate(" + pld + "deg)";
                break;
            case speedOpt:
                document.querySelector('.speed span').textContent = pld;
                break;
            case distanceOpt:
                document.querySelector('.dis span').textContent = pld;
                break;
            case licenseOpt:
                document.querySelector('.plateInfo').textContent = pld;
                break;
            case wallStopOpt:
                document.querySelector('.stop input').checked = pld;
                break
        }

//                console.log(opt, pld);
    };

    socket.onclose = function () {
        console.log('Socket Status: ' + socket.readyState + ' (Closed)');
    };
    var ongoingTouches = [];
    var speedTable = [
        [
            [1, 0],
            [1, 0],
            [1, 0],
            [1, 0],
            [1, 0],
            [1, 0]
        ],
        [
            [2, 0],
            [2, 0],
            [2, 0],
            [2, 0],
            [2, 0],
            [2, 0]
        ],
        [
            [3, 0],
            [3, 0],
            [3, 0],
            [3, 0],
            [3, 0],
            [3, 0]
        ],
        [
            [4, 0],
            [4, 0],
            [4, 0],
            [4, 0],
            [4, 0],
            [4, 0]
        ],
        [
            [5, 0],
            [5, 0],
            [5, 0],
            [5, 0],
            [5, 0],
            [5, 0]
        ],
        [
            [6, 0],
            [6, 0],
            [6, 0],
            [6, 0],
            [6, 0],
            [6, 0]
        ]
    ];
    var spacingSpeedTable = [
        [
            [0, 0],
            [1, 1],
            [2, 2],
            [3, 3],
            [4, 4],
            [5, 5],
        ],
        [
            [1, 0],
            [2, 0],
            [3, 0],
            [4, 0],
            [5, 0],
            [6, 0],
        ],
    ];

    function startup() {
        var el = document.body;
        el.addEventListener("touchstart", function (evt) {
            var stick = document.querySelector(".stick");
            var wrapper = document.querySelector(".joystick");
            var touches = evt.changedTouches;
            var offset = wrapper.getBoundingClientRect();

            for (var i = 0; i < touches.length; i++) {
                if (
                    touches[i].clientX - offset.left < offset.width
                    && touches[i].clientX - offset.left >= 0
                    && touches[i].clientY - offset.top >= 0
                    && touches[i].clientY - offset.top < offset.height
                ) {
                    evt.preventDefault();
                    ongoingTouches.push(copyTouch(touches[i]));
                }
            }
        }, false);
        el.addEventListener("touchend", touchEnd, false);
        el.addEventListener("touchcancel", touchEnd, false);
        el.addEventListener("touchleave", touchEnd, false);
        el.addEventListener("touchmove", function (evt) {
            var stick = document.querySelector(".stick");
            var wrapper = document.querySelector(".joystick");
            var touches = evt.changedTouches;
            var offset = wrapper.getBoundingClientRect();
            var offsetstick = stick.getBoundingClientRect();

            for (var i = 0; i < touches.length; i++) {
                var idx = ongoingTouchIndexById(touches[i].identifier);
                if (
                    touches[i].clientX - offset.left < offset.width
                    && touches[i].clientX - offset.left >= 0
                    && touches[i].clientY - offset.top >= 0
                    && touches[i].clientY - offset.top < offset.height
                ) {
                    evt.preventDefault();

                    if (idx >= 0) {
                        sendBuffer.set([motorOpt, calculateValue(touches[i].clientX - offset.left, touches[i].clientY - offset.top)]);
                        if (socket.readyState == 1) {
                            socket.send(sendBuffer);
                        }
                        var newX = touches[i].clientX - offset.left;
                        var newY = touches[i].clientY - offset.top;
                        stick.style.left = newX - offsetstick.width / 2 + "px";
                        stick.style.top = newY - offsetstick.height / 2 + "px";

                        ongoingTouches.splice(idx, 1, copyTouch(touches[i])); // swap in the new touch record

                    }
                }
            }

        }, false);
    }

    function touchEnd(evt) {

        var stick = document.querySelector(".stick");
        var wrapper = document.querySelector(".joystick");
        var offset = wrapper.getBoundingClientRect();
        var offsetstick = stick.getBoundingClientRect();

        stick.style.left = offset.width / 2 - offsetstick.width / 2 + "px";
        stick.style.top = offset.height / 2 - offsetstick.height / 2 + "px";
        calculateValue(offset.width / 2 - offsetstick.width / 2, offset.height / 2 - offsetstick.height / 2);
        sendBuffer.set([motorOpt, 0]);
        if (socket.readyState == 1){
            socket.send(sendBuffer);
        }
    }

    function copyTouch(touch) {
        return {identifier: touch.identifier, clientX: touch.clientX, clientY: touch.clientY};
    }

    function ongoingTouchIndexById(idToFind) {
        for (var i = 0; i < ongoingTouches.length; i++) {
            var id = ongoingTouches[i].identifier;

            if (id == idToFind) {
                return i;
            }
        }
        return -1;
    }

    function calculateValue(x, y) {
        var degrees = calculateDegrees(x, y);
        degrees += 90;
        if (degrees > 360) {
            degrees -= 360;
        }
        var distance = calculateDistance(x, y);
        if (distance == 0) {
            return 0;
        }

        var speed = distanceToSpeed(distance);
        var direction = degreesToDirection(degrees);
        return calculateEngineValue(direction, degrees, speed);

    }

    function calculateDegrees(x, y) {
        var stick = document.querySelector(".joystick").getBoundingClientRect();

        var deltaX = x - stick.width / 2;
        var deltaY = y - stick.height / 2;

        var rad = Math.atan2(deltaX, deltaY);
        rad += Math.PI / 2;
        var deg = toDegrees(rad);
        if (deg < 0) {
            deg += 360;
        }
        return parseInt(deg);
    }

    function calculateDistance(x, y) {
        var stick = document.querySelector(".joystick").getBoundingClientRect();

        var deltaX = x - stick.width / 2;
        var deltaY = y - stick.height / 2;

        var pointDelta = Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
        return parseInt(Math.sqrt(pointDelta));
    }

    function distanceToSpeed(distance) {
        var stick = document.querySelector(".joystick").getBoundingClientRect();

        var speed = Math.round(distance / ((stick.width / 2) / 6)) + 1;
        if (speed > 6) {
            speed = 6;
        }
        return speed;
    }

    function degreesToDirection(degrees) {
        if (degrees < 90 || degrees > 270)
            return 0; // forward
        return 1; // backward

    }

    function calculateEngineValue(direction, degrees, speed) {
        var piePart = parseInt(degrees / 90);

        var partNumber = 0;
        var spacing = 20;
        var partSize = parseInt(90 - parseInt(spacing * 2)) / 6;
        var spacingField = -1;

        for (var i = 0; i < 5; i++) {

            if ((i == 0) && (((degrees - parseInt(piePart * 90)) <= spacing))) {
                spacingField = 0;
                break;
            }
            if ((i == 0) && (parseInt(piePart * 90 + 90) - spacing < (degrees))) {
                spacingField = 1;
                break;
            }
            if (degrees - parseInt(piePart * 90) >= spacing + (parseInt(i * partSize)) && degrees - parseInt(piePart * 90) <= parseInt(spacing + parseInt(i * partSize) + partSize)) {
                break;
            }
            partNumber++;
        }
        var speeding = [];
        if (spacingField == -1) {
            speeding = speedTable[partNumber][(speed - 1)];
        } else {
            spacingField = ((piePart == 0 && spacingField == 0) || (piePart == 1 && spacingField == 1) || (piePart == 2 && spacingField == 0) || (piePart == 3 && spacingField == 1) ? 0 : 1);
            speeding = spacingSpeedTable[spacingField][(speed - 1)];
        }

        var left = 0;
        var right = 0;

        if (piePart == 0 || piePart == 1) {
            left = speeding[1];
            right = speeding[0];
        } else if (piePart == 2 || piePart == 3) {
            left = speeding[0];
            right = speeding[1];
        }


        var valueLeft = direction << 3 ^ left;
        var valueRight = direction << 3 ^ right;
        return valueLeft << 4 ^ valueRight;

    }

    function toDegrees(rad) {
        return rad * (180 / Math.PI);
    }
}, false);
