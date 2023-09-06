# File Operation Library

`LogFile` is based on `MoneRandomAccessFile` to read the file content into memory and process it further
through `ReadListener`.

`MLog` is used to handle exception stack information. It collects exception stack information into a local queue,
waiting to be retrieved.

This library serves as the foundation for MiLog (self-developed logging system).