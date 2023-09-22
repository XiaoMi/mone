/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.hera.operator.dto;

import lombok.Data;
import run.mone.hera.operator.bo.HeraObjectMeta;
import run.mone.hera.operator.bo.HeraSpec;

import java.io.Serializable;

/**
 * @author shanwb
 * @date 2023-02-09
 */
@Data
public class HeraOperatorDefineDTO implements Serializable {

    HeraSpec heraSpec;

    HeraObjectMeta heraMeta;

    /**
     * customResource has been created
     */
    Boolean crExists;

}
