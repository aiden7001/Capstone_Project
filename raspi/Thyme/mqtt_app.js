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
 * Created by ryeubi on 2015-11-20.
 */

var fs = require('fs');
var mqtt = require('mqtt');
var util = require('util');
var xml2js = require('xml2js');
var url = require('url');
var js2xmlparser = require('js2xmlparser');
var ip = require('ip');
var shortid = require('shortid');

const crypto = require('crypto');

// for TAS
var net = require('net');

var socket_arr = {};

var sh_mqtt_adn = require('./thyme/mqtt_adn');


global.resp_mqtt_client_arr = [];
global.req_mqtt_client_arr = [];
global.resp_mqtt_ri_arr = [];

global.resp_mqtt_path_arr = {};


mqtt_resp_connect(usecbhost);

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
                                    var req_topic = '/oneM2M/req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
                                    sh_mqtt_adn.crtci(req_topic, useaeid, usecbcseid, usebodytype, parent_path, '', content, function (status, res_body, mqtt_client, parent_path) {
                                        console.log('<---- x-m2m-rsc : ' + status + ' <----');

                                        var path_arr = parent_path.split('/');

                                        var ctname = path_arr[3];

                                        if (status == 5106 || status == 2001 || status == 4105) {
                                            socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
                                        }
                                        else if (status == 5000) {
                                            sh_state = 'crtae';
                                            socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
                                        }
                                        else if(status == 9999) {
                                            socket_arr[ctname].write('{\"ctname\":\"'+ctname+'\",\"con\":\"'+ res_body +'\"}');
                                        }
                                        else {
                                            socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
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
        //socket.write('hello from tcp server');
    }).listen(usetasport, function() {
        console.log('TCP Server (' + ip.address() + ') is listening on port ' + usetasport);
    });
});

function send_ack_to_tas(status, ctname) {
    if (status == 5106 || status == 2001 || status == 4105) {
        socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
    }
    else if (status == 5000) {
        sh_state = 'crtae';
        socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
    }
    else if(status == 9999) {
        socket_arr[ctname].write('{\"ctname\":\"'+ctname+'\",\"con\":\"'+ res_body +'\"}');
    }
    else {
        socket_arr[ctname].write('{\"ctname\":\"' + ctname + '\",\"con\":\"' + status + '\"}');
    }
}

function mqtt_resp_connect(brokerip) {
    var resp_topic = '/oneM2M/resp/'+useaeid+'/#';
    var req_topic = '/oneM2M/req/+/'+useaeid+'/#';

    var mqtt_client = mqtt.connect('mqtt://' + brokerip + ':' + usemqttport);
    resp_mqtt_client_arr.push(mqtt_client);

    mqtt_client.on('connect', function () {
        mqtt_client.subscribe(resp_topic);
        mqtt_client.subscribe(req_topic);

        console.log('subscribe resp_topic_temp as ' + resp_topic);
    });

    mqtt_client.on('message', function (topic, message) {
        var topic_arr = topic.split("/");
        if(topic_arr[5] != null) {
            var bodytype = (topic_arr[5] == 'xml') ? topic_arr[5] : ((topic_arr[5] == 'json') ? topic_arr[5] : 'json');
        }

        if(topic_arr[1] == 'oneM2M' && topic_arr[2] == 'resp' && topic_arr[3].replace(':', '/') == useaeid) {
            if(bodytype == 'xml') {
                var parser = new xml2js.Parser({explicitArray: false});
                parser.parseString(message.toString(), function (err, jsonObj) {
                    if (err) {
                        console.log('[pxymqtt-resp xml2js parser error]');
                    }
                    else {
                        if (jsonObj['m2m:rsp'] != null) {
                            for (var i = 0; i < resp_mqtt_ri_arr.length; i++) {
                                if (resp_mqtt_ri_arr[i] == jsonObj['m2m:rsp'].rqi) {
                                    var parent_path = resp_mqtt_path_arr[resp_mqtt_ri_arr[i]];
                                    callback_q[resp_mqtt_ri_arr[i]](jsonObj['m2m:rsp'].rsc, jsonObj['m2m:rsp'].pc, mqtt_client, parent_path);
                                    delete callback_q[resp_mqtt_ri_arr[i]];
                                    delete resp_mqtt_path_arr[resp_mqtt_ri_arr[i]];
                                    resp_mqtt_ri_arr.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        else {
                            NOPRINT == 'true' ? NOPRINT = 'true' : console.log('[pxymqtt-resp] message is not resp');
                            response_mqtt(mqtt_client, topic_arr[4], 4000, '', useaeid, rqi, '<h1>fail to parsing mqtt message</h1>');
                        }
                    }
                });
            }
            else { // 'json'
                var jsonObj = JSON.parse(message.toString());

                if (jsonObj['m2m:rsp'] != null) {
                    for (var i = 0; i < resp_mqtt_ri_arr.length; i++) {
                        if (resp_mqtt_ri_arr[i] == jsonObj['m2m:rsp'].rqi) {
                            var parent_path = resp_mqtt_path_arr[resp_mqtt_ri_arr[i]];
                            callback_q[resp_mqtt_ri_arr[i]](jsonObj['m2m:rsp'].rsc, jsonObj['m2m:rsp'].pc, mqtt_client, parent_path);
                            delete callback_q[resp_mqtt_ri_arr[i]];
                            delete resp_mqtt_path_arr[resp_mqtt_ri_arr[i]];
                            resp_mqtt_ri_arr.splice(i, 1);
                            break;
                        }
                    }
                }
            }
        }
        else if(topic_arr[1] == 'oneM2M' && topic_arr[2] == 'req' && topic_arr[4] == useaeid) {
            if(bodytype == 'xml') {
                parser = new xml2js.Parser({explicitArray: false});
                parser.parseString(message.toString(), function (err, jsonObj) {
                    if (err) {
                        console.log('[mqtt noti xml2js parser error]');
                    }
                    else {
                        if(jsonObj['m2m:rqp'].op == '5' || jsonObj['m2m:rqp'].op == 5) {
                            mqtt_noti_action(mqtt_client, topic_arr, jsonObj);
                        }
                    }
                });
            }
            else { // json
                jsonObj = JSON.parse(message.toString());
                mqtt_noti_action(mqtt_client, topic_arr, jsonObj);
            }
        }
        else {
            console.log('topic is not supported');
        }
    });
}

var sh_state = 'crtae';
var return_count = 0;
var request_count = 0;

function ae_response_action(status, result, mqtt_client) {
    var nmtype = (result['m2m:AE'] != null) ? 'long' : 'short';
    if(nmtype == 'long') {
        var aeid = result['m2m:AE']['AE-ID'];
    }
    else { // 'short'
        aeid = result['m2m:ae']['aei'];
    }

    console.log('x-m2m-rsc : ' + status + ' - ' + aeid + ' <----');

    for(var i = 0; i < resp_mqtt_client_arr.length; i++) {
        if(resp_mqtt_client_arr[i] == mqtt_client) {
            resp_mqtt_client_arr.splice(i, 1);
            break;
        }
    }

    mqtt_client.unsubscribe('/oneM2M/resp/'+useaeid+'/#');
    mqtt_client.end();

    useaeid = aeid;
    conf.ae.aeid = useaeid;
    fs.writeFileSync('conf.json', JSON.stringify(conf, null, 4), 'utf8');

    mqtt_resp_connect(usecbhost);
}

setInterval(function() {
    if(sh_state == 'crtae') {
        console.log('[sh_state] : ' + sh_state);
        if(resp_mqtt_client_arr.length == 0) {
            console.log('[mqtt connection is fail');
            return;
        }
        var parent_path = '/' + usecbname;
        var req_topic = '/oneM2M/reg_req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
        sh_mqtt_adn.crtae(req_topic, useaeid, usecbcseid, usebodytype, parent_path, useappname, useappid, function(status, res_body, mqtt_client) {
            if(status == 5106 || status == 2001) {
                ae_response_action(status, res_body, mqtt_client);
                sh_state = 'crtct';
            }
            else if(status == 4105) {
                console.log('x-m2m-rsc : ' + status + ' <----');
                sh_state = 'rtvae'
            }
        });
    }
    else if(sh_state == 'rtvae') {
        console.log('[sh_state] : ' + sh_state);
        var path = '/' + usecbname + '/' + useappname;
        req_topic = '/oneM2M/reg_req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
        sh_mqtt_adn.rtvae(req_topic, useaeid, usecbcseid, usebodytype, path, function(status, res_body, mqtt_client) {
            if(status == 5106 || status == 2000) {
                ae_response_action(status, res_body, mqtt_client);
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
            req_topic = '/oneM2M/req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
            sh_mqtt_adn.crtct(req_topic, useaeid, usecbcseid, usebodytype, parent_path, usectname[i].ctname, function(status, res_body) {
                console.log('x-m2m-rsc : ' + status + ' <----');
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
                req_topic = '/oneM2M/req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
                sh_mqtt_adn.delsub(req_topic, useaeid, usecbcseid, usebodytype, path, function(status) {
                    console.log('x-m2m-rsc : ' + status + ' <----');
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
                if(url.parse(usesubname[i]['nu']).protocol == 'mqtt:') {
                    if(url.parse(usesubname[i]['nu']).hostname == 'autoset') {
                        usesubname[i]['nu'] = 'mqtt://' + usecbhost + '/' + useaeid;
                    }
                }
                request_count++;
                parent_path = '/' + usecbname + usesubname[i].parentpath;
                req_topic = '/oneM2M/req/'+useaeid+'/'+usecbcseid.replace('/', ':')+'/'+usebodytype;
                sh_mqtt_adn.crtsub(req_topic, useaeid, usecbcseid, usebodytype, parent_path, usesubname[i]['subname'], usesubname[i]['nu'], function(status) {
                    console.log('x-m2m-rsc : ' + status + ' <----');
                    if(status == 5106 || status == 2001 || status == 4105) {
                        return_count++;
                        if(return_count == request_count) {
                            sh_state = 'crtci';
                            console.log('[sh_state] : ' + sh_state);

                            tas_ready.emit('connect');
                        }
                    }
                });
            }
        }

        if(request_count == 0) {
            sh_state = 'crtci';
            console.log('[sh_state] : ' + sh_state);

            tas_ready.emit('connect');
        }

    }
    else if(sh_state == 'crtci') {

    }
}, 1000);


function send_tas(path_arr, cinObj) {
    var cin = {};
    cin.ctname = path_arr[3];
    cin.con = (cinObj.con != null) ? cinObj.con : cinObj.content;

    if(cin.con == '') {
        console.log('---- is not cin message');
    }
    else {
        //console.log(JSON.stringify(cin));
        console.log('<---- send to tas');

        if (socket_arr[path_arr[3]] != null) {
            socket_arr[path_arr[3]].write(JSON.stringify(cin));
        }
    }
}


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

function mqtt_noti_action(mqtt_client, topic_arr, jsonObj) {
    if (jsonObj != null) {
        var op = (jsonObj['m2m:rqp'].op == null) ? '' : jsonObj['m2m:rqp'].op;
        var to = (jsonObj['m2m:rqp'].to == null) ? '' : jsonObj['m2m:rqp'].to;
        var fr = (jsonObj['m2m:rqp'].fr == null) ? '' : jsonObj['m2m:rqp'].fr;
        var rqi = (jsonObj['m2m:rqp'].rqi == null) ? '' : jsonObj['m2m:rqp'].rqi;
        var pc = (jsonObj['m2m:rqp'].pc == null) ? '' : jsonObj['m2m:rqp'].pc;

        var nmtype = pc['sgn'] != null ? 'short' : 'long';
        var sgnObj = pc['sgn'] != null ? pc['sgn'] : pc['singleNotification'];

        var bodytype = topic_arr[5];
        if (sgnObj != null && sgnObj != '') {
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
                if (sgnObj.nev.rep.cin == null) {
                    cinObj.con = '';
                }
                else {
                    cinObj.con = sgnObj.nev.rep.cin.con;
                }
            }

            for(var i = 0; i < usesubname.length; i++) {
                if (usesubname[i].parentpath.split('/')[2] == path_arr[3]) {
                    if (usesubname[i].subname == path_arr[4]) {
                        var rsp_topic = '/oneM2M/resp/' + topic_arr[3] + '/' + topic_arr[4] + '/' + topic_arr[5];
                        response_mqtt(mqtt_client, rsp_topic, 2001, '', useaeid, rqi, '', topic_arr[5]);

                        //console.log((cinObj.con != null ? cinObj.con : cinObj.content));
                        console.log('mqtt ' + bodytype + ' ' + nmtype + ' notification <----');

                        //send_tweet(cinObj);
                        send_tas(path_arr, cinObj);
                    }
                }
            }
        }
        else {
            console.log('[mqtt-noti] message is not mine');
        }
    }
    else {
        console.log('[mqtt-noti] message is not noti');
    }
}
