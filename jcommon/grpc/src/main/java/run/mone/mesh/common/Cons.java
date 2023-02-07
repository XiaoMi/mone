/*
 *  Copyright 2020 Xiaomi
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

package run.mone.mesh.common;

import io.grpc.Attributes;
import io.grpc.Metadata;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/27 11:12
 */
public abstract class Cons {

    public static Metadata.Key<String> SIDE_CAR_TOKEN = Metadata.Key.of("side_car_token", Metadata.ASCII_STRING_MARSHALLER);

    public static Metadata.Key<String> SIDE_CAR_APP = Metadata.Key.of("side_car_app", Metadata.ASCII_STRING_MARSHALLER);

    public static Attributes.Key<String> ATTR_SIDE_CAR_APP = Attributes.Key.create("attr_side_car_app");


}
