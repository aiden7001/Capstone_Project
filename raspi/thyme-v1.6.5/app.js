/**
 * Copyright (c) 2015, OCEAN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by ryeubi on 2015-08-31.
 */

var http = require('http');
var express = require('express');
var fs = require('fs');
var bodyParser = require('body-parser');
var mqtt = require('mqtt');
var util = require('util');
var xml2js = require('xml2js');
var url = require('url');
var js2xmlparser = require('js2xmlparser');
var ip = require('ip');
var shortid = require('shortid');

// for TAS
var net = require('net');

var sh_adn = require('./adn');
var noti = require('./noti');

var socket_arr = {};

var HTTP_SUBSCRIPTION_ENABLE = 0;
var MQTT_SUBSCRIPTION_ENABLE = 0;


var app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(bodyParser.json({ type: 'application/*+json' }));
app.use(bodyParser.text({ type: 'application/*+xml' }));

// ?????? ????????.
var server = null;
var noti_topic = '';

// ready for mqtt
for(var i = 0; i < usesubname.length; i++) {
    if(usesubname[i].subname != null) {
        if(url.parse(usesubname[i].nu).protocol == 'http:') {
            HTTP_SUBSCRIPTION_ENABLE = 1;
            if(url.parse(usesubname[i]['nu']).hostname == 'autoset') {
                usesubname[i]['nu'] = 'http://' + ip.address() + ':' + useappport + url.parse(usesubname[i]['nu']).pathname;
            }
        }
        else if(url.parse(usesubname[i].nu).protocol == 'mqtt:') {
            MQTT_SUBSCRIPTION_ENABLE = 1;
        }
        else {
            console.log('notification uri of subscription is not supported');
            process.exit();
        }
    }
}

global.sh_state = 'crtae';
var return_count = 0;
var request_count = 0;

function ready_for_notification() {
    if(HTTP_SUBSCRIPTION_ENABLE == 1) {
        server = http.createServer(app);
        server.listen(useappport, function () {
            console.log('coap_server running at ' + useappport + ' port');
        });
    }

    else if(MQTT_SUBSCRIPTION_ENABLE == 1) {
        for(var i = 0; i < usesubname.length; i++) {
            if (usesubname[i].subname != null) {
                if (url.parse(usesubname[i]['nu']).hostname == 'autoset') {
                    usesubname[i]['nu'] = 'mqtt://' + usecbhost + '/' + useaeid;
                    noti_topic = util.format('/oneM2M/req/+/%s/#', useaeid);
                }
                else if (url.parse(usesubname[i]['nu']).hostname == usecbhost) {
                    noti_topic = util.format('/oneM2M/req/+/%s/#', useaeid);
                }
                else {
                    noti_topic = util.format('%s', url.parse(usesubname[i].nu).pathname);
                }
            }
        }
        mqtt_connect(usecbhost, noti_topic);
    }

    fs.writeFileSync(conf_file_name, JSON.stringify(conf, null, 4), 'utf8');
}

function ae_response_action(status, res_body) {
    if(usebodytype == 'xml') {
        var message = res_body;
        var parser = new xml2js.Parser({explicitArray: false});
        parser.parseString(message.toString(), function (err, result) {
            if (err) {
                console.log('[rtvae xml2js parser error]');
            }
            else {
                var nmtype = (result['m2m:AE'] != null) ? 'long' : 'short';
                if(nmtype == 'long') {
                    var aeid = result['m2m:AE']['AE-ID'];
                }
                else { // 'short'
                    aeid = result['m2m:ae']['aei'];
                }

                console.log('x-m2m-rsc : ' + status + ' - ' + aeid + ' <----');
                useaeid = aeid;
                conf.ae.aeid = useaeid;
                fs.writeFileSync(conf_file_name, JSON.stringify(conf, null, 4), 'utf8');

                // if(HTTP_SUBSCRIPTION_ENABLE == 1) {
                //     server = http.createServer(app);
                //     server.listen(useappport, function () {
                //         console.log('coap_server running at ' + useappport + ' port');
                //     });
                // }
                //
                // else if(MQTT_SUBSCRIPTION_ENABLE == 1) {
                //     for(var i = 0; i < usesubname.length; i++) {
                //         if (usesubname[i].subname != null) {
                //             if (url.parse(usesubname[i]['nu']).hostname == 'autoset') {
                //                 usesubname[i]['nu'] = 'mqtt://' + usecbhost + '/' + useaeid;
                //                 noti_topic = util.format('/oneM2M/req/%s/%s/#', usecbcseid.replace('/', ':'), useaeid);
                //             }
                //             else {
                //                 if (url.parse(usesubname[i]['nu']).hostname == "") {
                //                     usesubname[i].nu = usesubname[i].nu.replace(/\/S\b/g, "/"+useaeid);
                //                     noti_topic = util.format('%s', url.parse(usesubname[i].nu).pathname);
                //                 }
                //                 else {
                //                     usesubname[i]['nu'] = 'mqtt://' + usecbhost + '/' + useaeid;
                //                     noti_topic = util.format('/oneM2M/req/%s/%s/#', usecbcseid.replace('/', ':'), useaeid);
                //                 }
                //             }
                //         }
                //     }
                //     //mqtt_connect(usecbhost, noti_topic);
                // }

            }
        });
    }
    else {
        var result = JSON.parse(res_body);
        var nmtype = (result['m2m:AE'] != null) ? 'long' : 'short';
        if(nmtype == 'long') {
            var aeid = result['m2m:AE']['AE-ID'];
        }
        else { // 'short'
            aeid = result['m2m:ae']['aei'];
        }

        console.log('x-m2m-rsc : ' + status + ' - ' + aeid + ' <----');
        useaeid = aeid;
        conf.ae.aeid = useaeid;
        fs.writeFileSync(conf_file_name, JSON.stringify(conf, null, 4), 'utf8');
        // if(HTTP_SUBSCRIPTION_ENABLE == 1) {
        //     server = http.createServer(app);
        //     server.listen(useappport, function () {
        //         console.log('coap_server running at ' + useappport + ' port');
        //     });
        // }
        //
        // else if(MQTT_SUBSCRIPTION_ENABLE == 1) {
        //     for(var i = 0; i < usesubname.length; i++) {
        //         if (usesubname[i].subname != null) {
        //             if (url.parse(usesubname[i]['nu']).hostname == 'autoset') {
        //                 usesubname[i]['nu'] = 'mqtt://' + usecbhost + '/' + useaeid;
        //                 noti_topic = util.format('/oneM2M/req/+/%s/#', useaeid);
        //             }
        //             else if (url.parse(usesubname[i]['nu']).hostname == usecbhost) {
        //                 noti_topic = util.format('/oneM2M/req/+/%s/#', useaeid);
        //             }
        //             else {
        //                 noti_topic = util.format('%s', url.parse(usesubname[i].nu).pathname);
        //             }
        //         }
        //     }
        //     //mqtt_connect(usecbhost, noti_topic);
        // }
    }
}

setInterval(function() {
    if(sh_state == 'crtae') {
        console.log('[sh_state] : ' + sh_state);
        var parent_path = '/' + usecbname;
        sh_adn.crtae(usecbhost, usecbport, parent_path, useappname, useappid, function(status, res_body) {
            console.log(res_body);
            if(status == 5106 || status == 2001) {
                ae_response_action(status, res_body);
                sh_state = 'crtct';
            }
            else if(status == 4105) {
                console.log('x-m2m-rsc : ' + status + ' <----');
                sh_state = 'rtvae'
            }
        });
    }
    else if(sh_state == 'rtvae') {
        if(useaeid == 'S') {
            useaeid = 'S' + shortid.generate();
        }

        console.log('[sh_state] : ' + sh_state);
        var path = '/' + usecbname + '/' + useappname;
        sh_adn.rtvae(usecbhost, usecbport, path, function(status, res_body) {
            if(status == 2000) {
                ae_response_action(status, res_body);
                sh_state = 'crtct';
            }
            else {
                console.log('x-m2m-rsc : ' + status + ' <----');
            }
        });
    }
    else if(sh_state == 'crtct') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;
        for(var i = 0; i < usectname.length; i++) {
            request_count++;
            parent_path = '/' + usecbname + usectname[i].parentpath;
            sh_adn.crtct(usecbhost, usecbport, parent_path, usectname[i]['ctname'], function(status, body) {
                console.log('x-m2m-rsc : ' + status + ' <----' + body);
                if(status == 5106 || status == 2001 || status == 4105) {
                    return_count++;
                    if(return_count == request_count) {
                        sh_state = 'delsub';
                    }
                }
            });
        }

        if(request_count == 0) {
            sh_state = 'delsub';
        }
    }
    else if(sh_state == 'delsub') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;
        for(i = 0; i < usesubname.length; i++) {
            if(usesubname[i]['subname'] != null) {
                request_count++;
                path = '/' + usecbname + usesubname[i].parentpath + '/' + usesubname[i]['subname'];
                sh_adn.delsub(usecbhost, usecbport, path, usesubname[i]['nu'], function(status, body) {
                    console.log('x-m2m-rsc : ' + status + ' <----' + body);
                    if(status == 5106 || status == 2002 || status == 2000 || status == 4105 || status == 4004) {
                        return_count++;
                        if(return_count == request_count) {
                            sh_state = 'crtsub';
                        }
                    }
                });
            }
        }

        if(request_count == 0) {
            sh_state = 'crtsub';
        }
    }

    else if(sh_state == 'crtsub') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;

        for(i = 0; i < usesubname.length; i++) {
            if(usesubname[i]['subname'] != null) {
                request_count++;
                parent_path = '/' + usecbname + usesubname[i].parentpath;
                sh_adn.crtsub(usecbhost, usecbport, parent_path, usesubname[i]['subname'], usesubname[i]['nu'], function(status, body) {
                    console.log('x-m2m-rsc : ' + status + ' <----' + body);
                    if(status == 5106 || status == 2001 || status == 4105) {
                        return_count++;
                        if(return_count == request_count) {
                            ready_for_notification();

                            sh_state = 'crtci';
                            console.log('[sh_state] : ' + sh_state);

                            //fs.writeFileSync(conf_file_name, JSON.stringify(conf, null, 4), 'utf8');

                            //mqtt_connect(usecbhost, noti_topic);

                            tas_ready.emit('connect');
                        }
                    }
                });
            }
        }

        if(request_count == 0) {
            ready_for_notification();

            sh_state = 'crtci';
            console.log('[sh_state] : ' + sh_state);

            tas_ready.emit('connect');
        }
    }
    else if(sh_state == 'crtci') {

    }
}, 1000);


var tas_ready = new process.EventEmitter();
tas_ready.on('connect', function() {
    var buffers = {};

    net.createServer(function (socket) {
        console.log('socket connected');
        socket.id = Math.random() * 1000;
        buffers[socket.id] = '';
        socket.on('data', function(data) {
            // 'this' refers to the socket calling this callback.
            buffers[this.id] += data.toString();
            //console.log(buffers[this.id]);
            var data_arr = buffers[this.id].split('}');
            //console.log(data_arr[1]);
            if(data_arr.length >= 2) {
                buffers[this.id] = '';
                for (var i = 0; i < data_arr.length-1; i++) {
                    var line = data_arr[i];
                    line += '}';
                    var jsonObj = JSON.parse(line);
                    var ctname = jsonObj.ctname;
                    var content = jsonObj.con;


                    socket_arr[ctname] = socket;

                    console.log('----> got data for [' + ctname + '] from tas ---->');

                    if (jsonObj.con == 'hello') {
                        socket.write(line);
                    }
                    else {
                        if (sh_state == 'crtci') {
                            for (var j = 0; j < usectname.length; j++) {
                                if (usectname[j].ctname == ctname) {
                                    //console.log(line);
                                    var parent_path = '/' + usecbname + usectname[j].parentpath + '/' + ctname;
                                    sh_adn.crtci(usecbhost, usecbport, parent_path, '', content, socket, function (status, ctname, res_body) {
                                        console.log('x-m2m-rsc : ' + status + ' <----');
                                        if (status == 5106 || status == 2001 || status == 4105) {
                                            socket.write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
                                        }
                                        else if (status == 5000) {
                                            sh_state = 'crtae';
                                            socket.write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
                                        }
                                        else if(status == 9999) {
                                            socket.write('{\"ctname\":\"'+ctname+'\",\"con\":\"'+ res_body +'\"}');
                                        }
                                        else {
                                            socket.write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
        socket.on('end', function() {
            console.log('end');
        });
        socket.on('close', function() {
            console.log('close');
        });
        socket.on('error', function(e) {
            console.log('error ', e);
        });
        //socket.write('hello from tcp coap_server');
    }).listen(usetasport, function() {
        console.log('TCP Server (' + ip.address() + ') for TAS is listening on port ' + usetasport);
    });
});


// for notification
var xmlParser = bodyParser.text({ type: '*/*' });


function response_mqtt(mqtt_client, rsp_topic, rsc, to, fr, rqi, inpc, bodytype) {
    var rsp_message = {};
    rsp_message['m2m:rsp'] = {};
    rsp_message['m2m:rsp'].rsc = rsc;
    rsp_message['m2m:rsp'].to = to;
    rsp_message['m2m:rsp'].fr = fr;
    rsp_message['m2m:rsp'].rqi = rqi;
    rsp_message['m2m:rsp'].pc = inpc;

    if(bodytype == 'xml') {
        rsp_message['m2m:rsp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        var xmlString = js2xmlparser("m2m:rsp", rsp_message['m2m:rsp']);

        mqtt_client.publish(rsp_topic, xmlString);
    }
    else { // 'json'
        mqtt_client.publish(rsp_topic, JSON.stringify(rsp_message));
    }
}


function mqtt_connect(serverip, noti_topic) {
    var mqtt_client = mqtt.connect('mqtt://' + serverip + ':' + usemqttport);

    mqtt_client.on('connect', function () {
        mqtt_client.subscribe(noti_topic);
        console.log('[mqtt_connect] noti_topic : ' + noti_topic);
    });

    mqtt_client.on('message', function (topic, message) {

        var topic_arr = topic.split("/");

        var bodytype = usebodytype;
        if(topic_arr[5] != null) {
            bodytype = (topic_arr[5] == 'xml') ? topic_arr[5] : ((topic_arr[5] == 'json') ? topic_arr[5] : 'json');
        }

        if(topic_arr[1] == 'oneM2M' && topic_arr[2] == 'req' && topic_arr[4] == useaeid) {
            if(bodytype == 'xml') {
                var parser = new xml2js.Parser({explicitArray: false});
                parser.parseString(message.toString(), function (err, jsonObj) {
                    if (err) {
                        console.log('[mqtt noti xml2js parser error]');
                    }
                    else {
                        noti.mqtt_noti_action(mqtt_client, topic_arr, jsonObj, function (path_arr, cinObj, rqi) {
                            if(cinObj) {
                                for (var i = 0; i < usesubname.length; i++) {
                                    if (usesubname[i].parentpath.split('/')[2] == path_arr[path_arr.length-2]) {
                                        if (usesubname[i].subname == path_arr[path_arr.length-1]) {
                                            var rsp_topic = '/oneM2M/resp/' + topic_arr[3] + '/' + topic_arr[4] + '/' + topic_arr[5];
                                            response_mqtt(mqtt_client, rsp_topic, 2001, '', useaeid, rqi, '', topic_arr[5]);

                                            //console.log((cinObj.con != null ? cinObj.con : cinObj.content));
                                            console.log('mqtt ' + bodytype + ' notification <----');

                                            //send_tweet(cinObj);
                                            noti.send_tas(socket_arr, path_arr, cinObj);
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
            else { // json
                var jsonObj = JSON.parse(message.toString());
                noti.mqtt_noti_action(mqtt_client, topic_arr, jsonObj, function (path_arr, cinObj, rqi) {
                    if(cinObj) {
                        for (var i = 0; i < usesubname.length; i++) {
                            if (usesubname[i].parentpath.split('/')[2] == path_arr[path_arr.length-2]) {
                                if (usesubname[i].subname == path_arr[path_arr.length-1]) {
                                    var rsp_topic = '/oneM2M/resp/' + topic_arr[3] + '/' + topic_arr[4] + '/' + topic_arr[5];
                                    response_mqtt(mqtt_client, rsp_topic, 2001, '', useaeid, rqi, '', topic_arr[5]);

                                    //console.log((cinObj.con != null ? cinObj.con : cinObj.content));
                                    console.log('mqtt ' + bodytype + ' notification <----');

                                    //send_tweet(cinObj);
                                    noti.send_tas(socket_arr, path_arr, cinObj);
                                }
                            }
                        }
                    }
                });
            }
        }
        else {
            console.log('topic is not supported');
        }
    });
}

var noti_count = 0;
app.post('/:resourcename0', xmlParser, function(request, response, next) {
    noti_count = 0;
    for(var i = 0; i < usesubname.length; i++) {
        if(usesubname[i]['nu'] != null) {
            var nu_path = url.parse(usesubname[i]['nu']).pathname.toString().split('/')[1];
            if (nu_path == request.params.resourcename0) {
                var content_type = request.headers['content-type'];
                if((content_type != 'application/vnd.onem2m-ntfy+xml') && (content_type != 'application/vnd.onem2m-ntfy+json')) {
                    console.log('Bad Request : content-type');
                    response.setHeader('X-M2M-RSC', '4000');
                    response.status(400).end('<h1>Bad Request : content-type</h1>');
                    return;
                }

                noti_count++;
                console.log('[CO notification through http <-- ' + request.headers['x-m2m-origin'] + ']');

                var bodytype = content_type.split('+')[1];
                if (bodytype == 'json') {
                    var result = request.body;
                    var nmtype = result['m2m:sgn'] != null ? 'short' : 'long';
                    var sgnObj = result['m2m:sgn'] != null ? result['m2m:sgn'] : result['m2m:singleNotification'];

                    if(nmtype == 'long') {
                        var path_arr = sgnObj.subscriptionReference.split('/');
                        var cinObj = {};
                        if(sgnObj.notificationEvent.representation.contentInstance == null) {
                            cinObj.con = '';
                        }
                        else {
                            cinObj = sgnObj.notificationEvent.representation.contentInstance;
                        }
                    }
                    else { // 'short'
                        path_arr = sgnObj.sur.split('/');
                        cinObj = {};
                        if(sgnObj.nev.rep.cin) {
                            cinObj = sgnObj.nev.rep.cin;
                        }
                        else if(sgnObj.nev.rep['m2m:cin']) {
                            cinObj = sgnObj.nev.rep['m2m:cin'];
                        }
                        else {
                            cinObj.con = '';
                        }
                    }

                    for(var j = 0; j < usesubname.length; j++) {
                        if (usesubname[j].parentpath.split('/')[2] == path_arr[3]) {
                            if (usesubname[j].subname == path_arr[4]) {
                                response.setHeader('X-M2M-RSC', '2001');
                                response.setHeader('X-M2M-RI', request.headers['x-m2m-ri']);
                                response.status(201).end('<h1>success to receive notification</h1>');

                                //console.log((cinObj.con != null ? cinObj.con : cinObj.content));
                                console.log('http ' + bodytype + ' ' + nmtype + ' notification <----');

                                //send_tweet(cinObj);
                                noti.send_tas(socket_arr, path_arr, cinObj);
                            }
                        }
                    }
                }
                else {
                    console.log(request.body);
                    var parser = new xml2js.Parser({explicitArray: false});
                    parser.parseString(request.body.toString(), function (err, result) {
                        if (err) {
                            console.log("Parsing An error occurred trying to read in the file: " + err);
                            console.log("error : set to default for configuration")
                        }
                        else {
                            var nmtype = result['m2m:sgn'] != null ? 'short' : 'long';
                            var sgnObj = result['m2m:sgn'] != null ? result['m2m:sgn'] : result['m2m:singleNotification'];

                            if (nmtype == 'long') {
                                var path_arr = sgnObj.subscriptionReference.split('/');
                                var cinObj = {};
                                if (sgnObj.notificationEvent.representation.contentInstance == null) {
                                    cinObj.con = '';
                                }
                                else {
                                    cinObj.con = sgnObj.notificationEvent.representation.contentInstance.content;
                                }
                            }
                            else { // 'short'
                                path_arr = sgnObj.sur.split('/');
                                cinObj = {};
                                if(sgnObj.nev.rep.cin) {
                                    cinObj = sgnObj.nev.rep.cin;
                                }
                                else if(sgnObj.nev.rep['m2m:cin']) {
                                    cinObj = sgnObj.nev.rep['m2m:cin'];
                                }
                                else {
                                    cinObj.con = '';
                                }
                            }
                        }

                        for(var j = 0; j < usesubname.length; j++) {
                            if (usesubname[j].parentpath.split('/')[2] == path_arr[3]) {
                                if (usesubname[j].subname == path_arr[4]) {
                                    response.setHeader('X-M2M-RSC', '2001');
                                    response.setHeader('X-M2M-RI', request.headers['x-m2m-ri']);
                                    response.status(201).end('<h1>success to receive notification</h1>');

                                    //console.log((cinObj.con != null ? cinObj.con : cinObj.content));
                                    console.log('http ' + bodytype + ' ' + nmtype + ' notification <----');

                                    //send_tweet(cinObj);
                                    noti.send_tas(socket_arr, path_arr, cinObj);
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    if(noti_count == 0) {
        response.setHeader('X-M2M-RSC', '0404');
        if (request.headers['x-m2m-ri'] != null) {
            response.setHeader('X-M2M-RI', request.headers['x-m2m-ri']);
        }

        response.status(404).end('<h1>Do not support</h1>');
    }
});

app.get('/conf', xmlParser, function(request, response, next) {

});
