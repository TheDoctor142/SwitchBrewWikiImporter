//:3
//@author TheDoctor
//@category Helper
//@keybinding
//@menupath
//@toolbar

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.symbol.Reference;
import ghidra.program.model.symbol.SourceType;
import ghidra.util.exception.DuplicateNameException;
import ghidra.util.exception.InvalidInputException;

public class WikiImporter extends GhidraScript {
	   String CommandDataFile, ServerDataFile;
	    List<String> RawCommandList, RawServerDataList;
	    String[] RawCommandArray, RawServerDataArray;
	    String[] AdressArray, NameArray, IndexArray, MasterServerArray;

	@Override
	protected void run() throws Exception {
		//TODO: Add script code here
	      CommandDataFile =(askFile("Select Wiki Data File", "Done")).getCanonicalPath();
          ServerDataFile = (askFile("Select Server Data File", "Done")).getCanonicalPath();
       RawServerDataList=readFileIntoList(ServerDataFile);
       RawServerDataArray= new String[RawServerDataList.size()];
       RawServerDataList.toArray(RawServerDataArray);
       RawCommandList =readFileIntoList(CommandDataFile);
       RawCommandArray = new String[RawCommandList.size()];
       AdressArray = new String[RawCommandList.size()-1];
       NameArray = new String[RawCommandList.size()-1];
       IndexArray = new String[RawCommandList.size()-1];
       MasterServerArray = new String[RawCommandList.size()-1];
       RawCommandList.toArray(RawCommandArray);
       toDataArray(RawCommandArray);
       parseServerInfo();
       renameFunctions();
	}
	public void renameFunctions() {
		for(int i=0; i<AdressArray.length; i++) {
			Address MainPointer = toAddr(AdressArray[i]);
			
				try {
						getSymbolAt(MainPointer).setName(NameArray[i], SourceType.ANALYSIS);
						Instruction curInst = getInstructionAt(MainPointer);
						boolean instFound=false;
						for(int x=0; !instFound; x++) {
							curInst=getInstructionAfter(curInst);
							if(curInst.getFlowType().isJump()||curInst.getFlowType().isCall()) {
								printf(curInst.getAddress().toString());
								Reference[] refArray;
								refArray=getReferencesFrom(curInst.getAddress());
								Address destAddress = refArray[0].getToAddress();
								getSymbolAt(destAddress).setName(NameArray[i]+"JumpImpl", SourceType.ANALYSIS);
								instFound=true;
							}
						
						}
						
					} catch (DuplicateNameException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidInputException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		}
	}
	   public void parseServerInfo(){
	        boolean isFound=false;
	        int CommandStartIndexInArray=-1;
	        //find the specific server wanted
	        for(int i=0; !isFound; i++){
	            String tempTrimmedString = RawServerDataArray[i].trim();
	            if(tempTrimmedString.startsWith("'"+MasterServerArray[0]+"'")){
	                isFound=true;
	                CommandStartIndexInArray=i+1;
	           }

	        }
	        boolean ShouldContinue=true;
	        for(int q=0; ShouldContinue; q++){
	            int index = -10;
	            index=RawServerDataArray[CommandStartIndexInArray+q].indexOf("func");
	            if(index!=-1) {
	                String found;
	                found=RawServerDataArray[CommandStartIndexInArray+q].substring(index+7,index+7+12);
	                AdressArray[q]= found;
	            }else{
	                ShouldContinue=false;
	            }
	        }
	    }
	    public void toDataArray(String[] rawArray){
	        String ServerName = RawCommandArray[0];
	       for(int i = 1; i< RawCommandArray.length; i++){
	           int iMinus1 = i-1;
	           MasterServerArray[iMinus1]=ServerName;
	           String rawString = RawCommandArray[i];
	           StringBuilder cmdIndexBuilder = new StringBuilder();
	           boolean stillFindingDigets = true;
	           for(int x = 0; stillFindingDigets; x++){
	                if(Character.isDigit(rawString.charAt(x))){
	                    cmdIndexBuilder.append(rawString.charAt(x));
	                }else{
	                    stillFindingDigets=false;
	                }
	            }
	           IndexArray[iMinus1] = cmdIndexBuilder.toString();
	           //Remove # from strings
	           String semiSanitizedString = rawString.replace("#", "");
	           StringBuilder semiSanitizedStringBuilder = new StringBuilder(semiSanitizedString);
	           removeChar("(",")", semiSanitizedStringBuilder);
	           removeChar("[","]", semiSanitizedStringBuilder);
	           String temp = semiSanitizedStringBuilder.toString();
	           temp = temp.replace(cmdIndexBuilder, "");
	           temp = temp.trim();
	           NameArray[iMinus1]=temp;
	           semiSanitizedStringBuilder = new StringBuilder(temp);
	           printf(NameArray[iMinus1]+", "+IndexArray[iMinus1]);
	        }
	    }
	    public void removeChar (String charToRemove, String CharToRemoveEnd, StringBuilder SanitizerBuilder){
	        int charStart;
	        charStart=SanitizerBuilder.indexOf(charToRemove);
	        if(charStart!=-1){
	            int CharEnd = SanitizerBuilder.indexOf(CharToRemoveEnd, charStart);
	            CharEnd++;
	            SanitizerBuilder.delete(charStart,CharEnd);

	        }
	    }
	    public List<String> readFileIntoList(String file) {
	        List<String> lines = Collections.emptyList();
	        try {
	            lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return lines;
	    }
}
