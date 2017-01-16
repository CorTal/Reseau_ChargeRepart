#!bin/bash
xterm -hold -title "Proxy" -e "cd build; java server.Proxy" &
sleep 1
xterm -hold -title "Server1" -e "cd build; java server.Server 3006" &
sleep 1
xterm -hold -title "Server2" -e "cd build; java server.Server 3007" &
sleep 1
xterm -hold -title "Server3" -e "cd build; java server.Server 3008" &
sleep 1
xterm -title "Register" -e "cd build; java client.ClientRegisterServiceFibonacci localhost 3008; java client.ClientRegisterServiceFibonacci localhost 3007"
sleep 1
#xterm -hold -title "Use" -e "cd build; java client.ClientUseServiceFibonacci 3007 5; java client.ClientUseServiceFibonacci 3007 5; java client.ClientUseServiceFibonacci 3007 5; java client.ClientUseServiceFibonacci 3007 5; java client.ClientUseServiceFibonacci 3007 5" &
#xterm -hold -title "Use" -e "cd build; java client.ClientUseServiceFibonacci 3008 5; java client.ClientUseServiceFibonacci 3008 5; java client.ClientUseServiceFibonacci 3008 5; java client.ClientUseServiceFibonacci 3008 5; java client.ClientUseServiceFibonacci 3008 5" &
xterm -hold -title "Use" -e "cd build; java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5" &
xterm -hold -title "Use" -e "cd build; java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3008 5;sleep 1;java client.ClientUseServiceFibonacci localhost 3007 5" &

