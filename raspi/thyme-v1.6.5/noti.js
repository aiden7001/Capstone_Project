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
 * Created by Il Yeup, Ahn in KETI on 2016-09-21.
 */

exports.send_tweet = function(cinObj) {
    var Twitter = require('twitter');

    var twitter_client = new Twitter({
        consumer_key: 'tV4cipDkQcMzZh8RAWsEToDP2',
        consumer_secret: '1rAIO5DCuFnRkYVefjst2ULVStBl6Dfucs2AVBjo1pcSx8jROT',
        access_token_key: '4157451558-lo0rgStwJ3ewEi47TpmrWnoDBPIRB3hcHeNggEk',
        access_token_secret: 'KlmoKMSvcWPuX1mcmuOd1SIvh8DyLXQD9ja3NeMoVCzdl'
    });

    var params = {screen_name: 'gbsmfather'};
    twitter_client.get('statuses/user_timeline', params, function(error, tweets, response){
        if (!error) {
            console.log(tweets[0].text);
        }
    });

    var cur_d = new Date();
    var cur_o = cur_d.getTimezoneOffset() / (-60);
    cur_d.setHours(cur_d.getHours() + cur_o);
    var cur_time = cur_d.toISOString().replace(/\..+/, '');

    var con_arr = (cinObj.con != null ? cinObj.con : cinObj.content).split(',');

    if(cin.con == '') {
        console.log('---- is not cin message');
    }
    else {
        if (parseFloat(con_arr[1]) <= 40.0) {
            if (con_arr[2] != null) {
                var bitmap = new Buffer(con_arr[2], 'base64');
                fs.writeFileSync('decode.jpg', bitmap);

                twitter_client.post('media/upload', {media: bitmap}, function (error, media, response) {
                    if (error) {
                        console.log(error[0].message);
                        return;
                    }
                    // If successful, a media object will be returned.
                    console.log(media);

                    // Lets tweet it
                    var status = {
                        status: '[' + cur_time + '] Give me water ! - ' + con_arr[0] + '\'C , Humi: ' + con_arr[1] + '%',
                        media_ids: media.media_id_string // Pass the media id string
                    };

                    twitter_client.post('statuses/update', status, function (error, tweet, response) {
                        if (!error) {
                            console.log(tweet.text);
                        }
                    });
                });
            }
            else {
                twitter_client.post('statuses/update', {status: '[' + cur_time + '] Give me water ! - ' + con_arr[0] + '\'C , ' + con_arr[1] + '%'}, function (error, tweet, response) {
                    if (error) {
                        console.log(error[0].message);
                        return;
                    }
                    console.log(tweet.text);  // Tweet body.
                    //console.log(response);  // Raw response object.

                });
            }
        }
        else {
            if (con_arr[2] != null) {
                bitmap = new Buffer(con_arr[2], 'base64');
                fs.writeFileSync('decode.jpg', bitmap);

                twitter_client.post('media/upload', {media: bitmap}, function (error, media, response) {
                    if (error) {
                        console.log(error[0].message);
                        return;
                    }
                    // If successful, a media object will be returned.
                    console.log(media);

                    // Lets tweet it
                    var status = {
                        status: '[' + cur_time + '] Ryeubi\'s pot status is Temp: ' + con_arr[0] + '\'C , Humi: ' + con_arr[1] + '%',
                        media_ids: media.media_id_string // Pass the media id string
                    };

                    twitter_client.post('statuses/update', status, function (error, tweet, response) {
                        if (!error) {
                            console.log(tweet.text);
                        }
                    });
                });
            }
            else {
                twitter_client.post('statuses/update', {status: '[' + cur_time + '] Ryeubi\'s pot status is Temp: ' + con_arr[0] + '\'C , Humi: ' + con_arr[1] + '%'}, function (error, tweet, response) {
                    if (error) {
                        console.log(error.message);
                        return;
                    }
                    console.log(tweet.text);  // Tweet body.
                    //console.log(response);  // Raw response object.
                });
            }
        }
    }
};

exports.send_tas = function(socket_arr, path_arr, cinObj) {
    var cin = {};
    cin.ctname = path_arr[path_arr.length-2];
    cin.con = (cinObj.con != null) ? cinObj.con : cinObj.content;

    if(cin.con == '') {
        console.log('---- is not cin message');
    }
    else {
        //console.log(JSON.stringify(cin));
        console.log('<---- send to tas');

        if (socket_arr[path_arr[path_arr.length-2]] != null) {
            socket_arr[path_arr[path_arr.length-2]].write(JSON.stringify(cin));
        }
    }
};


exports.mqtt_noti_action = function(mqtt_client, topic_arr, jsonObj, callback) {
    if (jsonObj != null) {
        var bodytype = usebodytype;
        if(topic_arr[5] != null) {
            bodytype = topic_arr[5];
        }

        var op = (jsonObj['m2m:rqp']['op'] == null) ? '' : jsonObj['m2m:rqp']['op'];
        var to = (jsonObj['m2m:rqp']['to'] == null) ? '' : jsonObj['m2m:rqp']['to'];
        var fr = (jsonObj['m2m:rqp']['fr'] == null) ? '' : jsonObj['m2m:rqp']['fr'];
        var rqi = (jsonObj['m2m:rqp']['rqi'] == null) ? '' : jsonObj['m2m:rqp']['rqi'];
        var pc = {};
        pc = (jsonObj['m2m:rqp']['pc'] == null) ? '' : jsonObj['m2m:rqp']['pc'];

        if(pc['m2m:sgn']) {
            pc.sgn = {};
            pc.sgn = pc['m2m:sgn'];
            delete pc['m2m:sgn'];
        }

        if(pc.sgn) {
            var nmtype = pc['sgn'] != null ? 'short' : 'long';
            var sgnObj = {};
            var cinObj = {};
            sgnObj = pc['sgn'] != null ? pc['sgn'] : pc['singleNotification'];

            if (nmtype == 'long') {
                console.log('oneM2M spec. define only short name for resource')
            }
            else { // 'short'
                if (sgnObj.sur) {
                    var path_arr = sgnObj.sur.split('/');
                }

                if (sgnObj.nev) {
                    if (sgnObj.nev.rep) {
                        if (sgnObj.nev.rep['m2m:cin']) {
                            sgnObj.nev.rep.cin = sgnObj.nev.rep['m2m:cin'];
                            delete sgnObj.nev.rep['m2m:cin'];
                        }

                        if (sgnObj.nev.rep.cin) {
                            cinObj = sgnObj.nev.rep.cin;
                        }
                        else {
                            console.log('[mqtt_noti_action] m2m:cin is none')
                        }
                    }
                    else {
                        console.log('[mqtt_noti_action] rep tag of m2m:sgn.nev is none. m2m:notification format mismatch with oneM2M spec.')
                    }
                }
                else {
                    console.log('[mqtt_noti_action] nev tag of m2m:sgn is none. m2m:notification format mismatch with oneM2M spec.')
                }
            }

            callback(path_arr, cinObj, rqi);
        }
        else {
            console.log('[mqtt_noti_action] m2m:sgn tag is none. m2m:notification format mismatch with oneM2M spec.')
        }
    }
    else {
        console.log('[mqtt_noti_action] message is not noti');
    }
};
