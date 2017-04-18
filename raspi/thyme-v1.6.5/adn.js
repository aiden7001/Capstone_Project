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
var js2xmlparser = require("js2xmlparser");
var xml2js = require('xml2js');
var shortid = require('shortid');

var bodyStr = {};

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

        console.log(bodyString);
    }
    else {
        results_ae['m2m:ae'] = {};
        results_ae['m2m:ae'].api = appid;
        results_ae['m2m:ae'].rn = appname;
        results_ae['m2m:ae'].rr = 'true';
        //results_ae['m2m:ae'].acpi = '/mobius-yt/acp1';
        bodyString = JSON.stringify(results_ae);
    }

    var options = {
        hostname: cbhost,
        port: cbport,
        path: parent_path,
        method: 'post',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype+'; ty=2',
            'Content-Length' : bodyString.length
        }
    };

    bodyStr['crtae'] = '';
    var req = http.request(options, function (res) {
        //console.log('[crtae response : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['crtae'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['crtae']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    //console.log(xmlString);

    req.write(bodyString);
    req.end();
};

exports.rtvae = function (cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'get',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid
        }
    };

    var xmlString = '';

    bodyStr['rtvae'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['rtvae'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['rtvae']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });


    req.write(xmlString);
    req.end();
};


exports.udtae = function (cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'put',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    var xmlString = '';
    if(usebodytype == 'xml') {
        results_ae.lbl = 'seahorse';
        results_ae['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        xmlString = js2xmlparser("m2m:ae", results_ae);
    }
    else {
        results_ae['m2m:ae'] = {};
        results_ae['m2m:ae'].lbl = 'seahorse';
        xmlString = JSON.stringify(results_ae);
    }

    bodyStr['udtae'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['udtae'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['udtae']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    console.log(xmlString);
    req.write(xmlString);
    req.end();
};


exports.delae = function (cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'delete',
        headers: {
            'locale': 'ko',
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    var xmlString = '';

    bodyStr['delae'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['delae'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['delae']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    req.write(xmlString);
    req.end();
};

exports.crtct = function(cbhost, cbport, parent_path, ctname, callback) {
    var requestid = shortid.generate();

    var results_ct = {};

    var xmlString = '';
    if(usebodytype == 'xml') {
        //results_ct.rn = ctname;
        results_ct.lbl = ctname;
        results_ct['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
            "rn": ctname
        };

        xmlString = js2xmlparser("m2m:cnt", results_ct);
    }
    else {
        results_ct['m2m:cnt'] = {};
        results_ct['m2m:cnt'].rn = ctname;
        results_ct['m2m:cnt'].lbl = [ctname];
        xmlString = JSON.stringify(results_ct);
    }

    var options = {
        hostname: cbhost,
        port: cbport,
        path: parent_path,
        method: 'post',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype+'; ty=3',
            'Content-Length' : xmlString.length
        }
    };

    bodyStr['crtct'] = '';
    var req = http.request(options, function (res) {
        console.log('[crtct response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['crtct'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['crtct']);
            bodyStr['crtct'] = '';
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    console.log(xmlString);
    req.write(xmlString);
    req.end();
};


exports.rtvct = function(cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var xmlString = '';

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'get',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    bodyStr['rtvct'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['rtvct'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['rtvct']);
            bodyStr['rtvct'] = '';
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    req.write(xmlString);
    req.end();
};


exports.udtct = function(cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var results_ct = {};
    var xmlString = '';
    if(usebodytype == 'xml') {
        results_ct.lbl = 'seahorese/'+ctname;
        results_ct['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        xmlString = js2xmlparser("m2m:cnt", results_ct);
    }
    else {
        results_ct['m2m:cnt'] = {};
        results_ct['m2m:cnt'].lbl = 'seahorese/'+ctname;
        xmlString = JSON.stringify(results_ct);
    }

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'put',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    bodyStr['udtct'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['udtct'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['udtct']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    console.log(xmlString);
    req.write(xmlString);
    req.end();
};


exports.delct = function(cbhost, cbport, path, callback) {
    var requestid = shortid.generate();

    var xmlString = '';

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'delete',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    bodyStr['delct'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['delct'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['delct']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    req.write(xmlString);
    req.end();
};


exports.delsub = function(cbhost, cbport, path, nu, callback) {
    var requestid = shortid.generate();

    var options = {
        hostname: cbhost,
        port: cbport,
        path: path,
        method: 'delete',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype
        }
    };

    bodyStr['delsub'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['delsub'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['delsub']);
            bodyStr['delsub'] = '';
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    req.write('');
    req.end();
};

exports.crtsub = function(cbhost, cbport, parent_path, subname, nu, callback) {
    var requestid = shortid.generate();

    var results_ss = {};
    var xmlString = '';
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

        xmlString = js2xmlparser("m2m:sub", results_ss);
    }
    else {
        results_ss['m2m:sub'] = {};
        results_ss['m2m:sub'].rn = subname;
        results_ss['m2m:sub'].enc = {net:[3]};
        results_ss['m2m:sub'].nu = [nu];
        results_ss['m2m:sub'].nct = 2;

        xmlString = JSON.stringify(results_ss);
    }

    var options = {
        hostname: cbhost,
        port: cbport,
        path: parent_path,
        method: 'post',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype+'; ty=23',
            'Content-Length' : xmlString.length
        }
    };

    bodyStr['crtsub'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['crtsub'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], bodyStr['crtsub']);
            bodyStr['crtsub'] = '';
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
        }
    });

    //console.log(xmlString);
    req.write(xmlString);
    req.end();
};

exports.crtci = function(cbhost, cbport, parent_path, ciname, content, socket, callback) {
    var requestid = shortid.generate();

    var results_ci = {};
    var xmlString = '';
    if(usebodytype == 'xml') {
        //results_ci.rn = (ciname != null && ciname != '') ? ciname : '';
        //var ci_nm = (ciname != null && ciname != '') ? ciname : '';
        results_ci.con = content;

        results_ci['@'] = {
            "xmlns:m2m": "http://www.onem2m.org/xml/protocols",
            "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance"
        };

        xmlString = js2xmlparser("m2m:cin", results_ci);
    }
    else {
        results_ci['m2m:cin'] = {};
        results_ci['m2m:cin'].rn = (ciname != null && ciname != '') ? ciname : '';
        results_ci['m2m:cin'].con = content;

        xmlString = JSON.stringify(results_ci);
    }

    var options = {
        hostname: cbhost,
        port: cbport,
        path: parent_path,
        method: 'post',
        headers: {
            'X-M2M-RI': requestid,
            'Accept': 'application/'+usebodytype,
            'X-M2M-Origin': useaeid,
            'Content-Type': 'application/vnd.onem2m-res+'+usebodytype+'; ty=4',
            'Content-Length' : xmlString.length
        }
    };

    var parent_path_arr = parent_path.split('/');

    bodyStr['crtci'] = '';
    var req = http.request(options, function (res) {
        //console.log('[rtvae response] : ' + res.statusCode);

        res.setEncoding('utf8');

        res.on('data', function (chunk) {
            bodyStr['crtci'] += chunk;
        });

        res.on('end', function () {
            callback(res.headers['x-m2m-rsc'], parent_path_arr[parent_path_arr.length-1], bodyStr['crtci']);
        });
    });

    req.on('error', function (e) {
        if(e.message != 'read ECONNRESET') {
            console.log('problem with request: ' + e.message);
            callback(9999, parent_path_arr[parent_path_arr.length-1], e.message);
        }
    });

    //console.log(xmlString);
    req.write(xmlString);
    req.end();
};

