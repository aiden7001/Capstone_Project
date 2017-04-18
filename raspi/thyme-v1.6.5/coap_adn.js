/**
 * Copyright (c) 2016, OCEAN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by Il-Yeup Ahn on 2016-11-18.
 */

var js2xmlparser = require("js2xmlparser");
var xml2js = require('xml2js');
var shortid = require('shortid');
var coap = require('coap');

var responseBody = {};

exports.crtae = function (cbhost, cbport, parent_path, appname, appid, callback) {
    var requestid = shortid.generate();

    var results_ae = {};

    var bodyString = '';
    if(usebodytype == 'xml') {
        results_ae.api = appid;
        //results_ae.rn = appname;
        results_ae.rr = 'true';
        results_ae['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
            "rn" : appname
        };

        bodyString = js2xmlparser("m2m:ae", results_ae);

    }
    else {
        results_ae['m2m:ae'] = {};
        results_ae['m2m:ae'].api = appid;
        results_ae['m2m:ae'].rn = appname;
        results_ae['m2m:ae'].rr = 'true';
        //results_ae['m2m:ae'].acpi = '/mobius-yt/acp1';
        bodyString = JSON.stringify(results_ae);
    }

    console.log(bodyString + ' ----->');

    var options = {
        host: cbhost,
        port: 7591,
        pathname: parent_path,
        method: 'post',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype,
            'Content-Type': 'application/'+usebodytype+'; ty=2',
            'Content-Length' : bodyString.length
        }
    };

    responseBody['crtae'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.setOption("267", new Buffer('2'));    // X-M2M-TY
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['crtae'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, responseBody['crtae']);
            responseBody['crtae'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};

exports.rtvae = function (cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var options = {
        host: cbhost,
        port: 7591,
        pathname: path,
        method: 'get',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype
        }
    };

    var bodyString = '';
    responseBody['rtvae'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['rtvae'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, responseBody['rtvae']);
            responseBody['rtvae'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};


exports.crtct = function(cbhost, cbport, parent_path, ctname, callback) {
    var requestid = shortid.generate();

    var results_ct = {};

    var bodyString = '';
    if(usebodytype == 'xml') {
        //results_ct.rn = ctname;
        results_ct.lbl = ctname;
        results_ct['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
            "rn": ctname
        };

        bodyString = js2xmlparser("m2m:cnt", results_ct);
    }
    else {
        results_ct['m2m:cnt'] = {};
        results_ct['m2m:cnt'].rn = ctname;
        results_ct['m2m:cnt'].lbl = [ctname];
        bodyString = JSON.stringify(results_ct);
    }

    console.log(bodyString + ' ----->');

    var options = {
        host: cbhost,
        port: 7591,
        pathname: parent_path,
        method: 'post',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype,
            'Content-Type': 'application/'+usebodytype+'; ty=3',
            'Content-Length' : bodyString.length
        }
    };

    responseBody['crtct'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.setOption("267", new Buffer('3'));    // X-M2M-TY
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['crtct'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, responseBody['crtct']);
            responseBody['crtct'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};

exports.delsub = function(cbhost, cbport, path, nu, callback) {
    var requestid = shortid.generate();

    var options = {
        host: cbhost,
        port: 7591,
        pathname: path,
        method: 'delete',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype
        }
    };

    var bodyString = '';
    responseBody['delsub'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['delsub'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, responseBody['delsub']);
            responseBody['delsub'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};

exports.crtsub = function(cbhost, cbport, parent_path, subname, nu, callback) {
    var requestid = shortid.generate();

    var results_ss = {};

    var bodyString = '';
    if(usebodytype == 'xml') {
        //results_ss.rn = subname;
        results_ss.enc = {net:3};
        results_ss.nu = nu;
        results_ss.nct = 2;
        results_ss['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
            "rn": subname
        };

        bodyString = js2xmlparser("m2m:sub", results_ss);
    }
    else {
        results_ss['m2m:sub'] = {};
        results_ss['m2m:sub'].rn = subname;
        results_ss['m2m:sub'].enc = {net:[3]};
        results_ss['m2m:sub'].nu = [nu];
        results_ss['m2m:sub'].nct = 2;

        bodyString = JSON.stringify(results_ss);
    }

    console.log(bodyString + ' ----->');

    var options = {
        host: cbhost,
        port: 7591,
        pathname: parent_path,
        method: 'post',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype,
            'Content-Type': 'application/'+usebodytype+'; ty=23',
            'Content-Length' : bodyString.length
        }
    };

    responseBody['crtsub'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.setOption("267", new Buffer('23'));    // X-M2M-TY
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['crtsub'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, responseBody['crtsub']);
            responseBody['crtsub'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};

exports.crtci = function(cbhost, cbport, parent_path, ciname, content, socket, callback) {
    var requestid = shortid.generate();

    var results_ci = {};
    var bodyString = '';
    if(usebodytype == 'xml') {
        //results_ci.rn = (ciname != null && ciname != '') ? ciname : '';
        //var ci_nm = (ciname != null && ciname != '') ? ciname : '';
        results_ci.con = content;

        results_ci['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        bodyString = js2xmlparser("m2m:cin", results_ci);
    }
    else {
        results_ci['m2m:cin'] = {};
        results_ci['m2m:cin'].rn = (ciname != null && ciname != '') ? ciname : '';
        results_ci['m2m:cin'].con = content;

        bodyString = JSON.stringify(results_ci);
    }

    console.log(bodyString + ' ----->');

    var options = {
        host: cbhost,
        port: 7591,
        pathname: parent_path,
        method: 'post',
        confirmable: 'true',
        options: {
            'Accept': 'application/'+usebodytype,
            'Content-Type': 'application/'+usebodytype+'; ty=4',
            'Content-Length' : bodyString.length
        }
    };

    var parent_path_arr = parent_path.split('/');

    responseBody['crtci'] = '';
    var req = coap.request(options);
    req.setOption("256", new Buffer(useaeid));      // X-M2M-Origin
    req.setOption("257", new Buffer(requestid));    // X-M2M-RI
    req.setOption("267", new Buffer('4'));    // X-M2M-TY
    req.on('response', function (res) {
        res.on('data', function () {
            responseBody['crtci'] += res.payload.toString();
        });

        res.on('end', function () {
            callback(res.code, parent_path_arr[parent_path_arr.length-1], responseBody['crtci']);
            responseBody['crtci'] = '';
        });
    });

    req.write(bodyString);
    req.end();
};

