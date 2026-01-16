"use strict";
// Copyright 2021-2025 Buf Technologies, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
Object.defineProperty(exports, "__esModule", { value: true });
exports.symbols = exports.wktPublicImportPaths = exports.packageName = void 0;
/**
 * @private
 */
exports.packageName = "@bufbuild/protobuf";
/**
 * @private
 */
exports.wktPublicImportPaths = {
    "google/protobuf/compiler/plugin.proto": exports.packageName + "/wkt",
    "google/protobuf/any.proto": exports.packageName + "/wkt",
    "google/protobuf/api.proto": exports.packageName + "/wkt",
    "google/protobuf/descriptor.proto": exports.packageName + "/wkt",
    "google/protobuf/duration.proto": exports.packageName + "/wkt",
    "google/protobuf/empty.proto": exports.packageName + "/wkt",
    "google/protobuf/field_mask.proto": exports.packageName + "/wkt",
    "google/protobuf/source_context.proto": exports.packageName + "/wkt",
    "google/protobuf/struct.proto": exports.packageName + "/wkt",
    "google/protobuf/timestamp.proto": exports.packageName + "/wkt",
    "google/protobuf/type.proto": exports.packageName + "/wkt",
    "google/protobuf/wrappers.proto": exports.packageName + "/wkt",
};
/**
 * @private
 */
// prettier-ignore
exports.symbols = {
    isMessage: { typeOnly: false, bootstrapWktFrom: "../../is-message.js", from: exports.packageName },
    Message: { typeOnly: true, bootstrapWktFrom: "../../types.js", from: exports.packageName },
    create: { typeOnly: false, bootstrapWktFrom: "../../create.js", from: exports.packageName },
    fromJson: { typeOnly: false, bootstrapWktFrom: "../../from-json.js", from: exports.packageName },
    fromJsonString: { typeOnly: false, bootstrapWktFrom: "../../from-json.js", from: exports.packageName },
    fromBinary: { typeOnly: false, bootstrapWktFrom: "../../from-binary.js", from: exports.packageName },
    toBinary: { typeOnly: false, bootstrapWktFrom: "../../to-binary.js", from: exports.packageName },
    toJson: { typeOnly: false, bootstrapWktFrom: "../../to-json.js", from: exports.packageName },
    toJsonString: { typeOnly: false, bootstrapWktFrom: "../../to-json.js", from: exports.packageName },
    protoInt64: { typeOnly: false, bootstrapWktFrom: "../../proto-int64.js", from: exports.packageName },
    JsonValue: { typeOnly: true, bootstrapWktFrom: "../../json-value.js", from: exports.packageName },
    JsonObject: { typeOnly: true, bootstrapWktFrom: "../../json-value.js", from: exports.packageName },
    codegen: {
        boot: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/boot.js", from: exports.packageName + "/codegenv1" },
        fileDesc: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/file.js", from: exports.packageName + "/codegenv1" },
        enumDesc: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/enum.js", from: exports.packageName + "/codegenv1" },
        extDesc: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/extension.js", from: exports.packageName + "/codegenv1" },
        messageDesc: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/message.js", from: exports.packageName + "/codegenv1" },
        serviceDesc: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/service.js", from: exports.packageName + "/codegenv1" },
        tsEnum: { typeOnly: false, bootstrapWktFrom: "../../codegenv1/enum.js", from: exports.packageName + "/codegenv1" },
        GenFile: { typeOnly: true, bootstrapWktFrom: "../../codegenv1/types.js", from: exports.packageName + "/codegenv1" },
        GenEnum: { typeOnly: true, bootstrapWktFrom: "../../codegenv1/types.js", from: exports.packageName + "/codegenv1" },
        GenExtension: { typeOnly: true, bootstrapWktFrom: "../../codegenv1/types.js", from: exports.packageName + "/codegenv1" },
        GenMessage: { typeOnly: true, bootstrapWktFrom: "../../codegenv1/types.js", from: exports.packageName + "/codegenv1" },
        GenService: { typeOnly: true, bootstrapWktFrom: "../../codegenv1/types.js", from: exports.packageName + "/codegenv1" },
    },
};
