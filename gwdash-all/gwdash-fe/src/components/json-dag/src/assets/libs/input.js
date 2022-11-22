/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

const json = {
  'taskList': [{
    'index': 0,
    'taskId': 0,
    'dependList': [],
    'status': 0,
    'data': {
      'id': 24,
      'url': '/test/one',
      'paramExtract': {
        'a': 'request_0_params.param{a}'
      },
      'paramMap': {},
      'httpMethod': 'GET',
      'result': false
    }
  }, {
    'index': 1,
    'taskId': 0,
    'dependList': [],
    'status': 0,
    'data': {
      'id': 30,
      'url': '/test/two',
      'paramExtract': {
        'b': 'request_0_params.param{b}'
      },
      'paramMap': {},
      'httpMethod': 'GET',
      'result': false
    }
  }, {
    'index': 2,
    'taskId': 0,
    'dependList': [],
    'status': 0,
    'data': {
      'id': 31,
      'url': '/test/sum',
      'paramExtract': {
        'a': 'response_0_result.toMap(){data}',
        'b': 'response_1_result.toMap(){data}'
      },
      'paramMap': {},
      'httpMethod': 'GET',
      'result': true
    }
  }],
  'dependList': [{
    'from': 0,
    'to': 2
  }, {
    'from': 1,
    'to': 2
  }]
}
