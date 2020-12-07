package com.ruinscraft.buildcomputil;

import com.github.intellectualsites.plotsquared.commands.CommandDeclaration;
import com.github.intellectualsites.plotsquared.plot.commands.CommandCategory;
import com.github.intellectualsites.plotsquared.plot.commands.RequiredType;
import com.github.intellectualsites.plotsquared.plot.commands.SubCommand;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

@CommandDeclaration(
        command = "claim",
        category = CommandCategory.CLAIMING,
        usage = "/plot claim",
        permission = "plots.claim",
        description = "Disabled.",
        requiredType = RequiredType.NONE)
public class PlotClaim extends SubCommand {

    @Override
    public boolean onCommand(final PlotPlayer sender, String[] args) {
        sender.sendMessage("Command disabled");
        return false;
    }

}
