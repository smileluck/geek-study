@echo off
set /a port = "%3"
echo port %port%
echo start: '%1' ,end: '%2', port: '%port'
for /l %%I in (%1,1,%2) do (
    echo slot: '%%I'
   .\redis-cli -p %port% cluster addslots %%I
)
pause