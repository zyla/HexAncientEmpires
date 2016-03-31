# Based on: https://github.com/karulis/pybluez/blob/master/examples/simple/rfcomm-server.py

import sys
from bluetooth import *
import thread

uuid = "3d9ee4b4-f508-11e5-8a8f-b74a5cbde83f"

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

client_sock = None

def handle_input():
    while True:
        line = sys.stdin.readline()
        if client_sock:
            print('< %s' % line)
            client_sock.send(line)

thread.start_new_thread(handle_input, ())

while True:

    client_sock, client_info = server_sock.accept()
    print("Accepted connection from %s" % (client_info,))

    try:
        while True:
            line = client_sock.recv(1024).strip()
            print('> %s' % line)
    except IOError as e:
        print(e)

    print("disconnected")

    client_sock.close()
    client_sock = None

server_sock.close()
print("all done")
