## LDAP Asset Client

A Java client that queries a real LDAP directory server for asset data (vehicle max speed), constructing and parsing raw LDAP protocol packets (ASN.1 BER encoding) by hand - no JNDI, no LDAP libraries.

Directory setup (not Java - one-time server-side step)

## How the client works
BerWriter / BerReader hand-implement ASN.1 BER encoding/decoding - tag/length/value structures - for the specific LDAP operations needed: BindRequest, SearchRequest, SearchResultEntry, SearchResultDone, UnbindRequest.
The client connects to port 389, performs an anonymous simple bind, sends a SearchRequest filtering on (cn=<asset name>), and reads SearchResultEntry messages until SearchResultDone, extracting the requested attribute value.
All requests are written to the socket as raw bytes; all responses are read and unpacked using byte array operations - no library abstracts the wire protocol.

## Running
bash

javac *.java

java LDAPServer 

on the second termnial

javac *.java

java LDAPClient

