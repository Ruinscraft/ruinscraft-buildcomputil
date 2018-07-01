package com.ruinscraft.buildcomputil;

import com.intellectualcrafters.plot.commands.CommandCategory;
import com.intellectualcrafters.plot.commands.RequiredType;
import com.intellectualcrafters.plot.commands.SubCommand;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "auto", 
		category = CommandCategory.CLAIMING, 
		usage = "/plot auto",
		permission = "plots.auto", 
		description = "Start building and competing!",
		requiredType = RequiredType.NONE)
public class PlotAuto extends SubCommand {

	@Override
	public boolean onCommand(final PlotPlayer sender, String[] args) {
		if (sender.getPlots().size() >= 1) {
			BuildCompUtil.sendPrefixMessage(sender, "You already have a plot claimed! Use /p home.");
			return false;
		}
		PlotArea plotArea = sender.getPlotAreaAbs();
		for (int y = 2; y < 50000 / 107; y++) {
			int x = 0;
			Plot plot = plotArea.getPlot(new PlotId(x, y));
			if (attemptClaim(plot, sender)) {
				return true;
			} else {
				x++;
				plot = plotArea.getPlot(new PlotId(x, y));
				if (attemptClaim(plot, sender)) {
					return true;
				}
			}
		}
		BuildCompUtil.sendPrefixMessage(sender, "Could not find a plot to claim.");
		return false;
	}

	private boolean attemptClaim(Plot plot, PlotPlayer player) {
		if (plot.canClaim(player) && plot.getOwners().isEmpty()) {
			plot.setOwner(player.getUUID());
			plot.claim(player, true, "", false);
			return true;
		}
		return false;
	}

}
