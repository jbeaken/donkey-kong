package org.mzuri.donkeykong.commands;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DeckCommand {
    DIFF("diff", List.of("--non-zero-exit-code")),
    SYNC("sync", List.of()),
    VALIDATE("validate",  List.of()),
    DUMP("dump", List.of());

    DeckCommand(String commandArgument, List<String> additionalArguments) {
        this.commandArgument = commandArgument;
        this.additionalArguments = additionalArguments;
    }

    private final String commandArgument;
    private final List<String> additionalArguments;
}
