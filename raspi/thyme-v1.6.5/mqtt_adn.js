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
 * Created by ryeubi on 2015-11-21.
 */

/**
 * Created by ryeubi on 2015-08-31.
 */

var http = require('http');
var js2xmlparser = require("js2xmlparser");
var xml2js = require('xml2js');
var shortid = require('shortid');


const crypto = require('crypto');

var bodyStr = {};

var _this = this;

global.callback_q = {};

exports.crtae = function (mqtt_client, req_topic, aeid, cbcseid, bodytype, parent_path, appname, appid, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = parent_path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '1'; // create
    req_message['m2m:rqp'].to = parent_path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].ty = '2'; // ae
    req_message['m2m:rqp'].pc = {};
    req_message['m2m:rqp'].pc['m2m:ae'] = {};
    req_message['m2m:rqp'].pc['m2m:ae'].rn = appname;
    req_message['m2m:rqp'].pc['m2m:ae'].api = appid;
    req_message['m2m:rqp'].pc['m2m:ae'].rr = 'true';

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        req_message['m2m:rqp'].pc['m2m:ae']['@'] = {"rn": appname};
        delete req_message['m2m:rqp'].pc['m2m:ae'].rn;

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};

exports.rtvae = function (mqtt_client, req_topic, aeid, cbcseid, bodytype, path, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '2'; // retrieve
    req_message['m2m:rqp'].to = path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].pc = {};

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};


exports.udtae = function (mqtt_client, path, callback) {
    // to do
};


exports.delae = function (mqtt_client, path, callback) {
    // to do
};

exports.crtct = function(mqtt_client, req_topic, aeid, cbcseid, bodytype, parent_path, ctname, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = parent_path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '1'; // create
    req_message['m2m:rqp'].to = parent_path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].ty = '3'; // cnt
    req_message['m2m:rqp'].pc = {};
    req_message['m2m:rqp'].pc['m2m:cnt'] = {};
    req_message['m2m:rqp'].pc['m2m:cnt'].rn = ctname;
    req_message['m2m:rqp'].pc['m2m:cnt'].lbl = [];
    req_message['m2m:rqp'].pc['m2m:cnt'].lbl.push(ctname);

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        req_message['m2m:rqp'].pc['m2m:cnt']['@'] = {"rn": ctname};
        delete req_message['m2m:rqp'].pc['m2m:cnt'].rn;

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};


exports.rtvct = function(mqtt_client, req_topic, aeid, cbcseid, bodytype, path, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '2'; // retrieve
    req_message['m2m:rqp'].to = path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].pc = {};

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};


exports.udtct = function(mqtt_client, path, callback) {
    // to do
};


exports.delct = function(mqtt_client, path, callback) {
    // to do
};


exports.delsub = function(mqtt_client, req_topic, aeid, cbcseid, bodytype, path, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '4'; // delete
    req_message['m2m:rqp'].to = path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].pc = {};

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};

exports.crtsub = function(mqtt_client, req_topic, aeid, cbcseid, bodytype, parent_path, subname, nu, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = parent_path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '1'; // create
    req_message['m2m:rqp'].to = parent_path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].ty = '23'; // sub
    req_message['m2m:rqp'].pc = {};
    req_message['m2m:rqp'].pc['m2m:sub'] = {};
    req_message['m2m:rqp'].pc['m2m:sub'].rn = subname;
    req_message['m2m:rqp'].pc['m2m:sub'].enc = {};
    req_message['m2m:rqp'].pc['m2m:sub'].enc.net = [];
    req_message['m2m:rqp'].pc['m2m:sub'].enc.net.push('3');
    req_message['m2m:rqp'].pc['m2m:sub'].nu = [];
    req_message['m2m:rqp'].pc['m2m:sub'].nu.push(nu);
    req_message['m2m:rqp'].pc['m2m:sub'].nct = '2';

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        req_message['m2m:rqp'].pc['m2m:sub']['@'] = {"rn": subname};
        delete req_message['m2m:rqp'].pc['m2m:sub'].rn;

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};

exports.crtci = function(mqtt_client, req_topic, aeid, cbcseid, bodytype, parent_path, ciname, content, callback) {
    var rqi = shortid.generate();

    callback_q[rqi] = callback;

    resp_mqtt_ri_arr.push(rqi);
    resp_mqtt_path_arr[rqi] = parent_path;

    var req_message = {};
    req_message['m2m:rqp'] = {};
    req_message['m2m:rqp'].op = '1'; // create
    req_message['m2m:rqp'].to = parent_path;
    req_message['m2m:rqp'].fr = aeid;
    req_message['m2m:rqp'].rqi = rqi;
    req_message['m2m:rqp'].ty = '4'; // cin
    req_message['m2m:rqp'].pc = {};
    req_message['m2m:rqp'].pc['m2m:cin'] = {};
    req_message['m2m:rqp'].pc['m2m:cin'].rn = (ciname != null && ciname != '') ? ciname : '';
    req_message['m2m:rqp'].pc['m2m:cin'].con = content;

    if (bodytype == 'xml') {
        req_message['m2m:rqp']['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        var xmlString = js2xmlparser("m2m:rqp", req_message['m2m:rqp']);

        mqtt_client.publish(req_topic, xmlString);

        console.log(req_topic + ' (' + rqi + ' - xml) ---->');
    }
    else { // 'json'
        mqtt_client.publish(req_topic, JSON.stringify(req_message));

        console.log(req_topic + ' (json) ---->');
    }
};

