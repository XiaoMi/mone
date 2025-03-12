package run.mone.mcp.hammerspoon.function.trigertrade.dto;

import lombok.Data;

/**
 * Business object representing an option symbol
 * 
 * @author shanwb
 * @date 2025-03-10
 */
@Data
public class OptionSymbolBO {
    private String symbol;
    private String name;
    private String underlyingSymbol;

    @Override
    public String toString() {
        return "OptionSymbolBO{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", underlyingSymbol='" + underlyingSymbol + '\'' +
                '}';
    }
}