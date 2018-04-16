----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    10:58:38 02/19/2018 
-- Design Name: 
-- Module Name:    sub_clock - Behavioral 
-- Project Name: 
-- Target Devices: 
-- Tool versions: 
-- Description: 
--
-- Dependencies: 
--
-- Revision: 
-- Revision 0.01 - File Created
-- Additional Comments: 
--
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--use IEEE.NUMERIC_STD.ALL;

-- Uncomment the following library declaration if instantiating
-- any Xilinx primitives in this code.
--library UNISIM;
--use UNISIM.VComponents.all;

entity sub_clock is
	 generic ( clock_length : natural := 8);
    Port ( clk : in  STD_LOGIC;
           RAZ : in  STD_LOGIC;
           out_clk : out  STD_LOGIC);
end sub_clock;

architecture Behavioral of sub_clock is

begin
	process (clk,RAZ) 
		variable cpt : natural;
	begin
		if RAZ='0' then
			cpt := clock_length;
		else 
			if rising_edge(clk) then
				if cpt = 0 then
					out_clk <= '1';
					cpt := clock_length-1;
				else
					out_clk <= '0';
					cpt := cpt - 1;
				end if;
			end if;
		end if;
	end process;
end Behavioral;

