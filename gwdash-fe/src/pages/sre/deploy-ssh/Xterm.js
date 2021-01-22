/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

import { Terminal } from 'xterm';
import 'xterm/dist/xterm.css';
import 'xterm/lib/addons/fullscreen/fullscreen.css';
// import '@/assets/css/index.css';

import * as fit from 'xterm/lib/addons/fit/fit';
import * as attach from 'xterm/lib/addons/attach/attach';

import * as fullscreen from 'xterm/lib/addons/fullscreen/fullscreen.js';

import * as search from 'xterm/lib/addons/search/search.js';

Terminal.applyAddon(fit);
Terminal.applyAddon(attach);
Terminal.applyAddon(fullscreen);
Terminal.applyAddon(search);


export default Terminal;
