package com.centeredinventory.client;

import com.centeredinventory.CenteredInventoryConfig;

import net.fabricmc.api.ClientModInitializer;

public class CenteredInventoryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CenteredInventoryConfig.load();
	}
}