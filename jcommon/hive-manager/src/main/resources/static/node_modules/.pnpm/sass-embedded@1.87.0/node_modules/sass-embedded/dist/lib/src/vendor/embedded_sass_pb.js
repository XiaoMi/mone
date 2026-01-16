"use strict";
// Copyright 2019 Google Inc. Use of this source code is governed by an
// MIT-style license that can be found in the LICENSE file or at
// https://opensource.org/licenses/MIT.
Object.defineProperty(exports, "__esModule", { value: true });
exports.SingletonValue = exports.ListSeparatorSchema = exports.ListSeparator = exports.ProtocolErrorTypeSchema = exports.ProtocolErrorType = exports.LogEventTypeSchema = exports.LogEventType = exports.SyntaxSchema = exports.Syntax = exports.OutputStyleSchema = exports.OutputStyle = exports.NodePackageImporterSchema = exports.Value_Calculation_CalculationOperationSchema = exports.Value_Calculation_CalculationValueSchema = exports.Value_CalculationSchema = exports.Value_ArgumentListSchema = exports.Value_CompilerMixinSchema = exports.Value_HostFunctionSchema = exports.Value_CompilerFunctionSchema = exports.Value_Map_EntrySchema = exports.Value_MapSchema = exports.Value_ListSchema = exports.Value_ColorSchema = exports.Value_NumberSchema = exports.Value_StringSchema = exports.ValueSchema = exports.SourceSpan_SourceLocationSchema = exports.SourceSpanSchema = exports.ProtocolErrorSchema = exports.OutboundMessage_FunctionCallRequestSchema = exports.OutboundMessage_FileImportRequestSchema = exports.OutboundMessage_ImportRequestSchema = exports.OutboundMessage_CanonicalizeRequestSchema = exports.OutboundMessage_LogEventSchema = exports.OutboundMessage_CompileResponse_CompileFailureSchema = exports.OutboundMessage_CompileResponse_CompileSuccessSchema = exports.OutboundMessage_CompileResponseSchema = exports.OutboundMessage_VersionResponseSchema = exports.OutboundMessageSchema = exports.InboundMessage_FunctionCallResponseSchema = exports.InboundMessage_FileImportResponseSchema = exports.InboundMessage_ImportResponse_ImportSuccessSchema = exports.InboundMessage_ImportResponseSchema = exports.InboundMessage_CanonicalizeResponseSchema = exports.InboundMessage_CompileRequest_ImporterSchema = exports.InboundMessage_CompileRequest_StringInputSchema = exports.InboundMessage_CompileRequestSchema = exports.InboundMessage_VersionRequestSchema = exports.InboundMessageSchema = exports.file_embedded_sass = void 0;
exports.CalculationOperatorSchema = exports.CalculationOperator = exports.SingletonValueSchema = void 0;
const codegenv1_1 = require("@bufbuild/protobuf/codegenv1");
/**
 * Describes the file embedded_sass.proto.
 */
exports.file_embedded_sass = (0, codegenv1_1.fileDesc)("ChNlbWJlZGRlZF9zYXNzLnByb3RvEhZzYXNzLmVtYmVkZGVkX3Byb3RvY29sIugQCg5JbmJvdW5kTWVzc2FnZRJQCg9jb21waWxlX3JlcXVlc3QYAiABKAsyNS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLkluYm91bmRNZXNzYWdlLkNvbXBpbGVSZXF1ZXN0SAASXAoVY2Fub25pY2FsaXplX3Jlc3BvbnNlGAMgASgLMjsuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5JbmJvdW5kTWVzc2FnZS5DYW5vbmljYWxpemVSZXNwb25zZUgAElAKD2ltcG9ydF9yZXNwb25zZRgEIAEoCzI1LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuSW5ib3VuZE1lc3NhZ2UuSW1wb3J0UmVzcG9uc2VIABJZChRmaWxlX2ltcG9ydF9yZXNwb25zZRgFIAEoCzI5LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuSW5ib3VuZE1lc3NhZ2UuRmlsZUltcG9ydFJlc3BvbnNlSAASXQoWZnVuY3Rpb25fY2FsbF9yZXNwb25zZRgGIAEoCzI7LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuSW5ib3VuZE1lc3NhZ2UuRnVuY3Rpb25DYWxsUmVzcG9uc2VIABJQCg92ZXJzaW9uX3JlcXVlc3QYByABKAsyNS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLkluYm91bmRNZXNzYWdlLlZlcnNpb25SZXF1ZXN0SAAaHAoOVmVyc2lvblJlcXVlc3QSCgoCaWQYASABKA0amAcKDkNvbXBpbGVSZXF1ZXN0ElMKBnN0cmluZxgCIAEoCzJBLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuSW5ib3VuZE1lc3NhZ2UuQ29tcGlsZVJlcXVlc3QuU3RyaW5nSW5wdXRIABIOCgRwYXRoGAMgASgJSAASMgoFc3R5bGUYBCABKA4yIy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLk91dHB1dFN0eWxlEhIKCnNvdXJjZV9tYXAYBSABKAgSUQoJaW1wb3J0ZXJzGAYgAygLMj4uc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5JbmJvdW5kTWVzc2FnZS5Db21waWxlUmVxdWVzdC5JbXBvcnRlchIYChBnbG9iYWxfZnVuY3Rpb25zGAcgAygJEhMKC2FsZXJ0X2NvbG9yGAggASgIEhMKC2FsZXJ0X2FzY2lpGAkgASgIEg8KB3ZlcmJvc2UYCiABKAgSEgoKcXVpZXRfZGVwcxgLIAEoCBIiChpzb3VyY2VfbWFwX2luY2x1ZGVfc291cmNlcxgMIAEoCBIPCgdjaGFyc2V0GA0gASgIEg4KBnNpbGVudBgOIAEoCBIZChFmYXRhbF9kZXByZWNhdGlvbhgPIAMoCRIbChNzaWxlbmNlX2RlcHJlY2F0aW9uGBAgAygJEhoKEmZ1dHVyZV9kZXByZWNhdGlvbhgRIAMoCRqsAQoLU3RyaW5nSW5wdXQSDgoGc291cmNlGAEgASgJEgsKA3VybBgCIAEoCRIuCgZzeW50YXgYAyABKA4yHi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlN5bnRheBJQCghpbXBvcnRlchgEIAEoCzI+LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuSW5ib3VuZE1lc3NhZ2UuQ29tcGlsZVJlcXVlc3QuSW1wb3J0ZXIaxQEKCEltcG9ydGVyEg4KBHBhdGgYASABKAlIABIVCgtpbXBvcnRlcl9pZBgCIAEoDUgAEhoKEGZpbGVfaW1wb3J0ZXJfaWQYAyABKA1IABJMChVub2RlX3BhY2thZ2VfaW1wb3J0ZXIYBSABKAsyKy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLk5vZGVQYWNrYWdlSW1wb3J0ZXJIABIcChRub25fY2Fub25pY2FsX3NjaGVtZRgEIAMoCUIKCghpbXBvcnRlckIHCgVpbnB1dEoECAEQAhprChRDYW5vbmljYWxpemVSZXNwb25zZRIKCgJpZBgBIAEoDRINCgN1cmwYAiABKAlIABIPCgVlcnJvchgDIAEoCUgAEh0KFWNvbnRhaW5pbmdfdXJsX3VudXNlZBgEIAEoCEIICgZyZXN1bHQakwIKDkltcG9ydFJlc3BvbnNlEgoKAmlkGAEgASgNElYKB3N1Y2Nlc3MYAiABKAsyQy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLkluYm91bmRNZXNzYWdlLkltcG9ydFJlc3BvbnNlLkltcG9ydFN1Y2Nlc3NIABIPCgVlcnJvchgDIAEoCUgAGoEBCg1JbXBvcnRTdWNjZXNzEhAKCGNvbnRlbnRzGAEgASgJEi4KBnN5bnRheBgCIAEoDjIeLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuU3ludGF4EhsKDnNvdXJjZV9tYXBfdXJsGAMgASgJSACIAQFCEQoPX3NvdXJjZV9tYXBfdXJsQggKBnJlc3VsdBpuChJGaWxlSW1wb3J0UmVzcG9uc2USCgoCaWQYASABKA0SEgoIZmlsZV91cmwYAiABKAlIABIPCgVlcnJvchgDIAEoCUgAEh0KFWNvbnRhaW5pbmdfdXJsX3VudXNlZBgEIAEoCEIICgZyZXN1bHQakAEKFEZ1bmN0aW9uQ2FsbFJlc3BvbnNlEgoKAmlkGAEgASgNEjAKB3N1Y2Nlc3MYAiABKAsyHS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlSAASDwoFZXJyb3IYAyABKAlIABIfChdhY2Nlc3NlZF9hcmd1bWVudF9saXN0cxgEIAMoDUIICgZyZXN1bHRCCQoHbWVzc2FnZSLLDwoPT3V0Ym91bmRNZXNzYWdlEjYKBWVycm9yGAEgASgLMiUuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5Qcm90b2NvbEVycm9ySAASUwoQY29tcGlsZV9yZXNwb25zZRgCIAEoCzI3LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuT3V0Ym91bmRNZXNzYWdlLkNvbXBpbGVSZXNwb25zZUgAEkUKCWxvZ19ldmVudBgDIAEoCzIwLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuT3V0Ym91bmRNZXNzYWdlLkxvZ0V2ZW50SAASWwoUY2Fub25pY2FsaXplX3JlcXVlc3QYBCABKAsyOy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLk91dGJvdW5kTWVzc2FnZS5DYW5vbmljYWxpemVSZXF1ZXN0SAASTwoOaW1wb3J0X3JlcXVlc3QYBSABKAsyNS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLk91dGJvdW5kTWVzc2FnZS5JbXBvcnRSZXF1ZXN0SAASWAoTZmlsZV9pbXBvcnRfcmVxdWVzdBgGIAEoCzI5LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuT3V0Ym91bmRNZXNzYWdlLkZpbGVJbXBvcnRSZXF1ZXN0SAASXAoVZnVuY3Rpb25fY2FsbF9yZXF1ZXN0GAcgASgLMjsuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5PdXRib3VuZE1lc3NhZ2UuRnVuY3Rpb25DYWxsUmVxdWVzdEgAElMKEHZlcnNpb25fcmVzcG9uc2UYCCABKAsyNy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLk91dGJvdW5kTWVzc2FnZS5WZXJzaW9uUmVzcG9uc2VIABqOAQoPVmVyc2lvblJlc3BvbnNlEgoKAmlkGAUgASgNEhgKEHByb3RvY29sX3ZlcnNpb24YASABKAkSGAoQY29tcGlsZXJfdmVyc2lvbhgCIAEoCRIeChZpbXBsZW1lbnRhdGlvbl92ZXJzaW9uGAMgASgJEhsKE2ltcGxlbWVudGF0aW9uX25hbWUYBCABKAkaogMKD0NvbXBpbGVSZXNwb25zZRJZCgdzdWNjZXNzGAIgASgLMkYuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5PdXRib3VuZE1lc3NhZ2UuQ29tcGlsZVJlc3BvbnNlLkNvbXBpbGVTdWNjZXNzSAASWQoHZmFpbHVyZRgDIAEoCzJGLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuT3V0Ym91bmRNZXNzYWdlLkNvbXBpbGVSZXNwb25zZS5Db21waWxlRmFpbHVyZUgAEhMKC2xvYWRlZF91cmxzGAQgAygJGjcKDkNvbXBpbGVTdWNjZXNzEgsKA2NzcxgBIAEoCRISCgpzb3VyY2VfbWFwGAIgASgJSgQIAxAEGnsKDkNvbXBpbGVGYWlsdXJlEg8KB21lc3NhZ2UYASABKAkSMAoEc3BhbhgCIAEoCzIiLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuU291cmNlU3BhbhITCgtzdGFja190cmFjZRgDIAEoCRIRCglmb3JtYXR0ZWQYBCABKAlCCAoGcmVzdWx0SgQIARACGvEBCghMb2dFdmVudBIyCgR0eXBlGAIgASgOMiQuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5Mb2dFdmVudFR5cGUSDwoHbWVzc2FnZRgDIAEoCRI1CgRzcGFuGAQgASgLMiIuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5Tb3VyY2VTcGFuSACIAQESEwoLc3RhY2tfdHJhY2UYBSABKAkSEQoJZm9ybWF0dGVkGAYgASgJEh0KEGRlcHJlY2F0aW9uX3R5cGUYByABKAlIAYgBAUIHCgVfc3BhbkITChFfZGVwcmVjYXRpb25fdHlwZUoECAEQAhqOAQoTQ2Fub25pY2FsaXplUmVxdWVzdBIKCgJpZBgBIAEoDRITCgtpbXBvcnRlcl9pZBgDIAEoDRILCgN1cmwYBCABKAkSEwoLZnJvbV9pbXBvcnQYBSABKAgSGwoOY29udGFpbmluZ191cmwYBiABKAlIAIgBAUIRCg9fY29udGFpbmluZ191cmxKBAgCEAMaQwoNSW1wb3J0UmVxdWVzdBIKCgJpZBgBIAEoDRITCgtpbXBvcnRlcl9pZBgDIAEoDRILCgN1cmwYBCABKAlKBAgCEAMajAEKEUZpbGVJbXBvcnRSZXF1ZXN0EgoKAmlkGAEgASgNEhMKC2ltcG9ydGVyX2lkGAMgASgNEgsKA3VybBgEIAEoCRITCgtmcm9tX2ltcG9ydBgFIAEoCBIbCg5jb250YWluaW5nX3VybBgGIAEoCUgAiAEBQhEKD19jb250YWluaW5nX3VybEoECAIQAxqOAQoTRnVuY3Rpb25DYWxsUmVxdWVzdBIKCgJpZBgBIAEoDRIOCgRuYW1lGAMgASgJSAASFQoLZnVuY3Rpb25faWQYBCABKA1IABIwCglhcmd1bWVudHMYBSADKAsyHS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlQgwKCmlkZW50aWZpZXJKBAgCEANCCQoHbWVzc2FnZSJlCg1Qcm90b2NvbEVycm9yEjcKBHR5cGUYASABKA4yKS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlByb3RvY29sRXJyb3JUeXBlEgoKAmlkGAIgASgNEg8KB21lc3NhZ2UYAyABKAkihwIKClNvdXJjZVNwYW4SDAoEdGV4dBgBIAEoCRJACgVzdGFydBgCIAEoCzIxLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuU291cmNlU3Bhbi5Tb3VyY2VMb2NhdGlvbhJDCgNlbmQYAyABKAsyMS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlNvdXJjZVNwYW4uU291cmNlTG9jYXRpb25IAIgBARILCgN1cmwYBCABKAkSDwoHY29udGV4dBgFIAEoCRo+Cg5Tb3VyY2VMb2NhdGlvbhIOCgZvZmZzZXQYASABKA0SDAoEbGluZRgCIAEoDRIOCgZjb2x1bW4YAyABKA1CBgoEX2VuZCL4EQoFVmFsdWUSNgoGc3RyaW5nGAEgASgLMiQuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZS5TdHJpbmdIABI2CgZudW1iZXIYAiABKAsyJC5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLk51bWJlckgAEjIKBGxpc3QYBSABKAsyIi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkxpc3RIABIwCgNtYXAYBiABKAsyIS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLk1hcEgAEjsKCXNpbmdsZXRvbhgHIAEoDjImLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuU2luZ2xldG9uVmFsdWVIABJLChFjb21waWxlcl9mdW5jdGlvbhgIIAEoCzIuLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuVmFsdWUuQ29tcGlsZXJGdW5jdGlvbkgAEkMKDWhvc3RfZnVuY3Rpb24YCSABKAsyKi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkhvc3RGdW5jdGlvbkgAEkMKDWFyZ3VtZW50X2xpc3QYCiABKAsyKi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkFyZ3VtZW50TGlzdEgAEkAKC2NhbGN1bGF0aW9uGAwgASgLMikuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZS5DYWxjdWxhdGlvbkgAEkUKDmNvbXBpbGVyX21peGluGA0gASgLMisuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZS5Db21waWxlck1peGluSAASNAoFY29sb3IYDiABKAsyIy5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkNvbG9ySAAaJgoGU3RyaW5nEgwKBHRleHQYASABKAkSDgoGcXVvdGVkGAIgASgIGkEKBk51bWJlchINCgV2YWx1ZRgBIAEoARISCgpudW1lcmF0b3JzGAIgAygJEhQKDGRlbm9taW5hdG9ycxgDIAMoCRqgAQoFQ29sb3ISDQoFc3BhY2UYASABKAkSFQoIY2hhbm5lbDEYAiABKAFIAIgBARIVCghjaGFubmVsMhgDIAEoAUgBiAEBEhUKCGNoYW5uZWwzGAQgASgBSAKIAQESEgoFYWxwaGEYBSABKAFIA4gBAUILCglfY2hhbm5lbDFCCwoJX2NoYW5uZWwyQgsKCV9jaGFubmVsM0IICgZfYWxwaGEahwEKBExpc3QSOAoJc2VwYXJhdG9yGAEgASgOMiUuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5MaXN0U2VwYXJhdG9yEhQKDGhhc19icmFja2V0cxgCIAEoCBIvCghjb250ZW50cxgDIAMoCzIdLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuVmFsdWUaogEKA01hcBI4CgdlbnRyaWVzGAEgAygLMicuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZS5NYXAuRW50cnkaYQoFRW50cnkSKgoDa2V5GAEgASgLMh0uc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZRIsCgV2YWx1ZRgCIAEoCzIdLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuVmFsdWUaHgoQQ29tcGlsZXJGdW5jdGlvbhIKCgJpZBgBIAEoDRotCgxIb3N0RnVuY3Rpb24SCgoCaWQYASABKA0SEQoJc2lnbmF0dXJlGAIgASgJGhsKDUNvbXBpbGVyTWl4aW4SCgoCaWQYASABKA0aoQIKDEFyZ3VtZW50TGlzdBIKCgJpZBgBIAEoDRI4CglzZXBhcmF0b3IYAiABKA4yJS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLkxpc3RTZXBhcmF0b3ISLwoIY29udGVudHMYAyADKAsyHS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlEkoKCGtleXdvcmRzGAQgAygLMjguc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5WYWx1ZS5Bcmd1bWVudExpc3QuS2V5d29yZHNFbnRyeRpOCg1LZXl3b3Jkc0VudHJ5EgsKA2tleRgBIAEoCRIsCgV2YWx1ZRgCIAEoCzIdLnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuVmFsdWU6AjgBGu8ECgtDYWxjdWxhdGlvbhIMCgRuYW1lGAEgASgJEk0KCWFyZ3VtZW50cxgCIAMoCzI6LnNhc3MuZW1iZWRkZWRfcHJvdG9jb2wuVmFsdWUuQ2FsY3VsYXRpb24uQ2FsY3VsYXRpb25WYWx1ZRqVAgoQQ2FsY3VsYXRpb25WYWx1ZRI2CgZudW1iZXIYASABKAsyJC5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLk51bWJlckgAEhAKBnN0cmluZxgCIAEoCUgAEhcKDWludGVycG9sYXRpb24YAyABKAlIABJTCglvcGVyYXRpb24YBCABKAsyPi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkNhbGN1bGF0aW9uLkNhbGN1bGF0aW9uT3BlcmF0aW9uSAASQAoLY2FsY3VsYXRpb24YBSABKAsyKS5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkNhbGN1bGF0aW9uSABCBwoFdmFsdWUa6gEKFENhbGN1bGF0aW9uT3BlcmF0aW9uEj0KCG9wZXJhdG9yGAEgASgOMisuc2Fzcy5lbWJlZGRlZF9wcm90b2NvbC5DYWxjdWxhdGlvbk9wZXJhdG9yEkgKBGxlZnQYAiABKAsyOi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkNhbGN1bGF0aW9uLkNhbGN1bGF0aW9uVmFsdWUSSQoFcmlnaHQYAyABKAsyOi5zYXNzLmVtYmVkZGVkX3Byb3RvY29sLlZhbHVlLkNhbGN1bGF0aW9uLkNhbGN1bGF0aW9uVmFsdWVCBwoFdmFsdWUiNAoTTm9kZVBhY2thZ2VJbXBvcnRlchIdChVlbnRyeV9wb2ludF9kaXJlY3RvcnkYASABKAkqKwoLT3V0cHV0U3R5bGUSDAoIRVhQQU5ERUQQABIOCgpDT01QUkVTU0VEEAEqKQoGU3ludGF4EggKBFNDU1MQABIMCghJTkRFTlRFRBABEgcKA0NTUxACKj8KDExvZ0V2ZW50VHlwZRILCgdXQVJOSU5HEAASFwoTREVQUkVDQVRJT05fV0FSTklORxABEgkKBURFQlVHEAIqOAoRUHJvdG9jb2xFcnJvclR5cGUSCQoFUEFSU0UQABIKCgZQQVJBTVMQARIMCghJTlRFUk5BTBACKj8KDUxpc3RTZXBhcmF0b3ISCQoFQ09NTUEQABIJCgVTUEFDRRABEgkKBVNMQVNIEAISDQoJVU5ERUNJREVEEAMqLwoOU2luZ2xldG9uVmFsdWUSCAoEVFJVRRAAEgkKBUZBTFNFEAESCAoETlVMTBACKkEKE0NhbGN1bGF0aW9uT3BlcmF0b3ISCAoEUExVUxAAEgkKBU1JTlVTEAESCQoFVElNRVMQAhIKCgZESVZJREUQA0IjCh9jb20uc2Fzc19sYW5nLmVtYmVkZGVkX3Byb3RvY29sUAFiBnByb3RvMw");
/**
 * Describes the message sass.embedded_protocol.InboundMessage.
 * Use `create(InboundMessageSchema)` to create a new message.
 */
exports.InboundMessageSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.VersionRequest.
 * Use `create(InboundMessage_VersionRequestSchema)` to create a new message.
 */
exports.InboundMessage_VersionRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 0);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.CompileRequest.
 * Use `create(InboundMessage_CompileRequestSchema)` to create a new message.
 */
exports.InboundMessage_CompileRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 1);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.CompileRequest.StringInput.
 * Use `create(InboundMessage_CompileRequest_StringInputSchema)` to create a new message.
 */
exports.InboundMessage_CompileRequest_StringInputSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 1, 0);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.CompileRequest.Importer.
 * Use `create(InboundMessage_CompileRequest_ImporterSchema)` to create a new message.
 */
exports.InboundMessage_CompileRequest_ImporterSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 1, 1);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.CanonicalizeResponse.
 * Use `create(InboundMessage_CanonicalizeResponseSchema)` to create a new message.
 */
exports.InboundMessage_CanonicalizeResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 2);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.ImportResponse.
 * Use `create(InboundMessage_ImportResponseSchema)` to create a new message.
 */
exports.InboundMessage_ImportResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 3);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.ImportResponse.ImportSuccess.
 * Use `create(InboundMessage_ImportResponse_ImportSuccessSchema)` to create a new message.
 */
exports.InboundMessage_ImportResponse_ImportSuccessSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 3, 0);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.FileImportResponse.
 * Use `create(InboundMessage_FileImportResponseSchema)` to create a new message.
 */
exports.InboundMessage_FileImportResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 4);
/**
 * Describes the message sass.embedded_protocol.InboundMessage.FunctionCallResponse.
 * Use `create(InboundMessage_FunctionCallResponseSchema)` to create a new message.
 */
exports.InboundMessage_FunctionCallResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 0, 5);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.
 * Use `create(OutboundMessageSchema)` to create a new message.
 */
exports.OutboundMessageSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.VersionResponse.
 * Use `create(OutboundMessage_VersionResponseSchema)` to create a new message.
 */
exports.OutboundMessage_VersionResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 0);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.CompileResponse.
 * Use `create(OutboundMessage_CompileResponseSchema)` to create a new message.
 */
exports.OutboundMessage_CompileResponseSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 1);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.CompileResponse.CompileSuccess.
 * Use `create(OutboundMessage_CompileResponse_CompileSuccessSchema)` to create a new message.
 */
exports.OutboundMessage_CompileResponse_CompileSuccessSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 1, 0);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.CompileResponse.CompileFailure.
 * Use `create(OutboundMessage_CompileResponse_CompileFailureSchema)` to create a new message.
 */
exports.OutboundMessage_CompileResponse_CompileFailureSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 1, 1);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.LogEvent.
 * Use `create(OutboundMessage_LogEventSchema)` to create a new message.
 */
exports.OutboundMessage_LogEventSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 2);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.CanonicalizeRequest.
 * Use `create(OutboundMessage_CanonicalizeRequestSchema)` to create a new message.
 */
exports.OutboundMessage_CanonicalizeRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 3);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.ImportRequest.
 * Use `create(OutboundMessage_ImportRequestSchema)` to create a new message.
 */
exports.OutboundMessage_ImportRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 4);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.FileImportRequest.
 * Use `create(OutboundMessage_FileImportRequestSchema)` to create a new message.
 */
exports.OutboundMessage_FileImportRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 5);
/**
 * Describes the message sass.embedded_protocol.OutboundMessage.FunctionCallRequest.
 * Use `create(OutboundMessage_FunctionCallRequestSchema)` to create a new message.
 */
exports.OutboundMessage_FunctionCallRequestSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 1, 6);
/**
 * Describes the message sass.embedded_protocol.ProtocolError.
 * Use `create(ProtocolErrorSchema)` to create a new message.
 */
exports.ProtocolErrorSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 2);
/**
 * Describes the message sass.embedded_protocol.SourceSpan.
 * Use `create(SourceSpanSchema)` to create a new message.
 */
exports.SourceSpanSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 3);
/**
 * Describes the message sass.embedded_protocol.SourceSpan.SourceLocation.
 * Use `create(SourceSpan_SourceLocationSchema)` to create a new message.
 */
exports.SourceSpan_SourceLocationSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 3, 0);
/**
 * Describes the message sass.embedded_protocol.Value.
 * Use `create(ValueSchema)` to create a new message.
 */
exports.ValueSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4);
/**
 * Describes the message sass.embedded_protocol.Value.String.
 * Use `create(Value_StringSchema)` to create a new message.
 */
exports.Value_StringSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 0);
/**
 * Describes the message sass.embedded_protocol.Value.Number.
 * Use `create(Value_NumberSchema)` to create a new message.
 */
exports.Value_NumberSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 1);
/**
 * Describes the message sass.embedded_protocol.Value.Color.
 * Use `create(Value_ColorSchema)` to create a new message.
 */
exports.Value_ColorSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 2);
/**
 * Describes the message sass.embedded_protocol.Value.List.
 * Use `create(Value_ListSchema)` to create a new message.
 */
exports.Value_ListSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 3);
/**
 * Describes the message sass.embedded_protocol.Value.Map.
 * Use `create(Value_MapSchema)` to create a new message.
 */
exports.Value_MapSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 4);
/**
 * Describes the message sass.embedded_protocol.Value.Map.Entry.
 * Use `create(Value_Map_EntrySchema)` to create a new message.
 */
exports.Value_Map_EntrySchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 4, 0);
/**
 * Describes the message sass.embedded_protocol.Value.CompilerFunction.
 * Use `create(Value_CompilerFunctionSchema)` to create a new message.
 */
exports.Value_CompilerFunctionSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 5);
/**
 * Describes the message sass.embedded_protocol.Value.HostFunction.
 * Use `create(Value_HostFunctionSchema)` to create a new message.
 */
exports.Value_HostFunctionSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 6);
/**
 * Describes the message sass.embedded_protocol.Value.CompilerMixin.
 * Use `create(Value_CompilerMixinSchema)` to create a new message.
 */
exports.Value_CompilerMixinSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 7);
/**
 * Describes the message sass.embedded_protocol.Value.ArgumentList.
 * Use `create(Value_ArgumentListSchema)` to create a new message.
 */
exports.Value_ArgumentListSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 8);
/**
 * Describes the message sass.embedded_protocol.Value.Calculation.
 * Use `create(Value_CalculationSchema)` to create a new message.
 */
exports.Value_CalculationSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 9);
/**
 * Describes the message sass.embedded_protocol.Value.Calculation.CalculationValue.
 * Use `create(Value_Calculation_CalculationValueSchema)` to create a new message.
 */
exports.Value_Calculation_CalculationValueSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 9, 0);
/**
 * Describes the message sass.embedded_protocol.Value.Calculation.CalculationOperation.
 * Use `create(Value_Calculation_CalculationOperationSchema)` to create a new message.
 */
exports.Value_Calculation_CalculationOperationSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 4, 9, 1);
/**
 * Describes the message sass.embedded_protocol.NodePackageImporter.
 * Use `create(NodePackageImporterSchema)` to create a new message.
 */
exports.NodePackageImporterSchema = (0, codegenv1_1.messageDesc)(exports.file_embedded_sass, 5);
/**
 * Possible ways to format the CSS output. The compiler is not required to
 * support all possible options; if the host requests an unsupported style, the
 * compiler should choose the closest supported style.
 *
 * @generated from enum sass.embedded_protocol.OutputStyle
 */
var OutputStyle;
(function (OutputStyle) {
    /**
     * Each selector and declaration is written on its own line.
     *
     * @generated from enum value: EXPANDED = 0;
     */
    OutputStyle[OutputStyle["EXPANDED"] = 0] = "EXPANDED";
    /**
     * The entire stylesheet is written on a single line, with as few characters
     * as possible.
     *
     * @generated from enum value: COMPRESSED = 1;
     */
    OutputStyle[OutputStyle["COMPRESSED"] = 1] = "COMPRESSED";
})(OutputStyle || (exports.OutputStyle = OutputStyle = {}));
/**
 * Describes the enum sass.embedded_protocol.OutputStyle.
 */
exports.OutputStyleSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 0);
/**
 * Possible syntaxes for a Sass stylesheet.
 *
 * @generated from enum sass.embedded_protocol.Syntax
 */
var Syntax;
(function (Syntax) {
    /**
     * The CSS-superset `.scss` syntax.
     *
     * @generated from enum value: SCSS = 0;
     */
    Syntax[Syntax["SCSS"] = 0] = "SCSS";
    /**
     * The indented `.sass` syntax.
     *
     * @generated from enum value: INDENTED = 1;
     */
    Syntax[Syntax["INDENTED"] = 1] = "INDENTED";
    /**
     * Plain CSS syntax that doesn't support any special Sass features.
     *
     * @generated from enum value: CSS = 2;
     */
    Syntax[Syntax["CSS"] = 2] = "CSS";
})(Syntax || (exports.Syntax = Syntax = {}));
/**
 * Describes the enum sass.embedded_protocol.Syntax.
 */
exports.SyntaxSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 1);
/**
 * The possible types of [LogEvent].
 *
 * @generated from enum sass.embedded_protocol.LogEventType
 */
var LogEventType;
(function (LogEventType) {
    /**
     * A warning for something other than a deprecated Sass feature. Often emitted
     * due to a stylesheet using the `@warn` rule.
     *
     * @generated from enum value: WARNING = 0;
     */
    LogEventType[LogEventType["WARNING"] = 0] = "WARNING";
    /**
     * A warning indicating that the stylesheet is using a deprecated Sass
     * feature. Compilers should not add text like "deprecation warning" to
     * deprecation warnings; it's up to the host to determine how to signal that
     * to the user.
     *
     * @generated from enum value: DEPRECATION_WARNING = 1;
     */
    LogEventType[LogEventType["DEPRECATION_WARNING"] = 1] = "DEPRECATION_WARNING";
    /**
     * A message generated by the user for their own debugging purposes.
     *
     * @generated from enum value: DEBUG = 2;
     */
    LogEventType[LogEventType["DEBUG"] = 2] = "DEBUG";
})(LogEventType || (exports.LogEventType = LogEventType = {}));
/**
 * Describes the enum sass.embedded_protocol.LogEventType.
 */
exports.LogEventTypeSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 2);
/**
 * Potential types of protocol errors.
 *
 * @generated from enum sass.embedded_protocol.ProtocolErrorType
 */
var ProtocolErrorType;
(function (ProtocolErrorType) {
    /**
     * A message was received that couldn't be decoded as an `InboundMessage` (for
     * the compiler) or `OutboundMessage` (for the host).
     *
     * @generated from enum value: PARSE = 0;
     */
    ProtocolErrorType[ProtocolErrorType["PARSE"] = 0] = "PARSE";
    /**
     * A message was received that violated a documented restriction, such as not
     * providing a mandatory field.
     *
     * @generated from enum value: PARAMS = 1;
     */
    ProtocolErrorType[ProtocolErrorType["PARAMS"] = 1] = "PARAMS";
    /**
     * Something unexpected went wrong within the endpoint.
     *
     * @generated from enum value: INTERNAL = 2;
     */
    ProtocolErrorType[ProtocolErrorType["INTERNAL"] = 2] = "INTERNAL";
})(ProtocolErrorType || (exports.ProtocolErrorType = ProtocolErrorType = {}));
/**
 * Describes the enum sass.embedded_protocol.ProtocolErrorType.
 */
exports.ProtocolErrorTypeSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 3);
/**
 * Different types of separators a list can have.
 *
 * @generated from enum sass.embedded_protocol.ListSeparator
 */
var ListSeparator;
(function (ListSeparator) {
    /**
     * List elements are separated by a comma.
     *
     * @generated from enum value: COMMA = 0;
     */
    ListSeparator[ListSeparator["COMMA"] = 0] = "COMMA";
    /**
     * List elements are separated by whitespace.
     *
     * @generated from enum value: SPACE = 1;
     */
    ListSeparator[ListSeparator["SPACE"] = 1] = "SPACE";
    /**
     * List elements are separated by a forward slash.
     *
     * @generated from enum value: SLASH = 2;
     */
    ListSeparator[ListSeparator["SLASH"] = 2] = "SLASH";
    /**
     * The list's separator hasn't yet been determined. This is only allowed for
     * singleton and empty lists.
     *
     * Singleton lists and empty lists don't have separators defined. This means
     * that list functions will prefer other lists' separators if possible.
     *
     * @generated from enum value: UNDECIDED = 3;
     */
    ListSeparator[ListSeparator["UNDECIDED"] = 3] = "UNDECIDED";
})(ListSeparator || (exports.ListSeparator = ListSeparator = {}));
/**
 * Describes the enum sass.embedded_protocol.ListSeparator.
 */
exports.ListSeparatorSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 4);
/**
 * Singleton SassScript values that have no internal state.
 *
 * @generated from enum sass.embedded_protocol.SingletonValue
 */
var SingletonValue;
(function (SingletonValue) {
    /**
     * The SassScript boolean true value.
     *
     * @generated from enum value: TRUE = 0;
     */
    SingletonValue[SingletonValue["TRUE"] = 0] = "TRUE";
    /**
     * The SassScript boolean false value.
     *
     * @generated from enum value: FALSE = 1;
     */
    SingletonValue[SingletonValue["FALSE"] = 1] = "FALSE";
    /**
     * The SassScript null value.
     *
     * @generated from enum value: NULL = 2;
     */
    SingletonValue[SingletonValue["NULL"] = 2] = "NULL";
})(SingletonValue || (exports.SingletonValue = SingletonValue = {}));
/**
 * Describes the enum sass.embedded_protocol.SingletonValue.
 */
exports.SingletonValueSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 5);
/**
 * An operator used in a calculation value's operation.
 *
 * @generated from enum sass.embedded_protocol.CalculationOperator
 */
var CalculationOperator;
(function (CalculationOperator) {
    /**
     * The addition operator.
     *
     * @generated from enum value: PLUS = 0;
     */
    CalculationOperator[CalculationOperator["PLUS"] = 0] = "PLUS";
    /**
     * The subtraction operator.
     *
     * @generated from enum value: MINUS = 1;
     */
    CalculationOperator[CalculationOperator["MINUS"] = 1] = "MINUS";
    /**
     * The multiplication operator.
     *
     * @generated from enum value: TIMES = 2;
     */
    CalculationOperator[CalculationOperator["TIMES"] = 2] = "TIMES";
    /**
     * The division operator.
     *
     * @generated from enum value: DIVIDE = 3;
     */
    CalculationOperator[CalculationOperator["DIVIDE"] = 3] = "DIVIDE";
})(CalculationOperator || (exports.CalculationOperator = CalculationOperator = {}));
/**
 * Describes the enum sass.embedded_protocol.CalculationOperator.
 */
exports.CalculationOperatorSchema = (0, codegenv1_1.enumDesc)(exports.file_embedded_sass, 6);
//# sourceMappingURL=embedded_sass_pb.js.map