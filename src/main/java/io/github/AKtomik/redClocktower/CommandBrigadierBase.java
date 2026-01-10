package io.github.AKtomik.redClocktower;

import java.util.List;

public abstract class CommandBrigadierBase extends SubBrigadierBase {
    public abstract String name();
    public abstract List<String> aliases();
}