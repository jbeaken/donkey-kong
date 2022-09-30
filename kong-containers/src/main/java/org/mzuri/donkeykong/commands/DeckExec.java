package org.mzuri.donkeykong.commands;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class DeckExec implements Command {

   final DeckCommand deckCommand;
   
   List<String> configFiles = new ArrayList<>();

   String kongWorkspace;

   Integer isVerbose;

   DeckExec(DeckCommand deckCommand) {
      this.deckCommand = deckCommand;
   }

   DeckExec(DeckCommand deckCommand, String kongWorkspace) {
      this.deckCommand = deckCommand;
      this.kongWorkspace = kongWorkspace;
   }

   DeckExec(DeckCommand deckCommand, String kongWorkspace,  List<String> configFiles) {
      this.deckCommand = deckCommand;
      this.kongWorkspace = kongWorkspace;
      this.configFiles = configFiles;
   }

   public void addSource( String config ) {
      configFiles.add(config);
   }

   public List<String> buildCommand( String kongAdminApiUrl ) {
      List<String> command = new ArrayList<>( List.of("deck", deckCommand.getCommandArgument()) );

      command.addAll( deckCommand.getAdditionalArguments() );

      command.add("--kong-addr");
      command.add(kongAdminApiUrl);

      if(isVerbose != null) {
         command.add("--verbose");
         command.add(isVerbose.toString());
      }

      return Collections.unmodifiableList(command);
   }
}
