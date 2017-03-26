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
 * Created by Il Yeup, Ahn in KETI on 2016-08-19.
 */

var fs = require('fs');
var shortid = require('shortid');


// this variables have default vales modified by conf.json
global.useprotocol = 'http';

global.usecbhost = '127.0.0.1';
global.usecbport = '7579';
global.usecbname = 'mobius-yt';
global.usecbcseid = '/mobius-yt';

global.usebodytype = 'xml';
global.useaeid = 'S';
global.useappid = '0.2.481.1.1';
global.useappname = 'iyahn';
global.useappport = 9727;
global.usectname = [];
global.usesubname = [];
global.usetasport = '3105';

global.usemqttport = '1883';
//global.usemqttport = '20003';


var data  = fs.readFileSync('conf.json', 'utf-8');

global.conf = JSON.parse(data);

useprotocol = conf.useprotocol;

usecbhost = conf.cse.cbhost;
usecbport = conf.cse.cbport;
usecbname = conf.cse.cbname;
usecbcseid = conf.cse.cbcseid;

if(conf.ae != null) {
    usebodytype = conf.ae.bodytype;
    useaeid = conf.ae.aeid;
    useappid = conf.ae.appid;
    useappname = conf.ae.appname;
    useappport = conf.ae.appport;
    usetasport = conf.ae.tasport;
}

if(useaeid == 'S') {
    //useaeid = 'S' + shortid.generate();
    //conf.ae.aeid = useaeid;

    //fs.writeFileSync('conf.json', JSON.stringify(conf, null, 4), 'utf8');
}

if(conf.cnt != null) {
    usectname = conf.cnt;
}

if(conf.sub != null) {
    usesubname = conf.sub;
}


// AE core
if(useprotocol == 'mqtt') {
    require('./mqtt_app');
}
else {
    require('./app');
}