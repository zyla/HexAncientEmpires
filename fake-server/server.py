# Based on: https://github.com/karulis/pybluez/blob/master/examples/simple/rfcomm-server.py

import sys
from bluetooth import *
import thread

uuid = "3d9ee4b4-f508-11e5-8a8f-b74a5cbde83f"

def handle_input():
    while True:
        line = sys.stdin.readline()
        if client_sock:
            print('< %s' % line)
            client_sock.send(line)

thread.start_new_thread(handle_input, ())

def handle_connection(client_sock):
    try:
        while True:
            line = client_sock.recv(1024).strip()
            print('> %s' % line)
    except IOError as e:
        print(e)

    print("disconnected")
    client_sock.close()

    client_sock = None


if len(sys.argv) > 1:

    addr = sys.argv[1]

    print "Connecting to", addr
    service_matches = find_service(uuid=uuid, address=addr)

    first_match = service_matches[0]
    host = first_match["host"]
    port = first_match["port"]

    client_sock = BluetoothSocket(RFCOMM)
    client_sock.connect((host, port))

    print "Connected"

    handle_connection(client_sock)

else:
    server_sock=BluetoothSocket( RFCOMM )
    server_sock.bind(("",PORT_ANY))
    server_sock.listen(1)

    port = server_sock.getsockname()[1]

    advertise_service( server_sock, "SampleServer",
                       service_id = uuid,
                       service_classes = [ uuid, SERIAL_PORT_CLASS ],
                       profiles = [ SERIAL_PORT_PROFILE ], 
                        )
                       
    print("RFCOMM channel=%d, UUID=%s" % (port, uuid))

    while True:

        client_sock, client_info = server_sock.accept()
        print("Accepted connection from %s" % (client_info,))

        handle_connection(client_sock)

    server_sock.close()
    print("all done")
