# SwitchBrewWikiImporter
A Ghidra script to help import IPC names from the SwitchBrew wiki.
How to use: Copy and paste a table containing IPC commands from the switchbrew wiki and save them into a file. In the first line of the file, put the name of the service you are implementing. For example, "nn::nifm::detail::IGeneralService". An example of what this should look like can be found in CommandDataRaw.txt.

Go to https://yls8.mtheall.com and copy the swipcgen_server_modern.info for the IPC server you want to use. Copy and paste this into a file WITHOUT MAKING ANY CHANGES WHATSOEVER. An example of what this should look like can be found in ServerDataRaw.txt.

Open Ghidra, open your firmware you want to RE, and add WikiImporter.java as a script. Run this script. When it says "Select Wiki Data File", select the table of commands you copied from the SwitchBrew wiki. It will then say "Select Server Data File". Select the file that contains the copy-pasted swipcgen_server_modern.info. After this, your IPC calls should be properly named!


