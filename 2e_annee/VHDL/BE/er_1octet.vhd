----------------------------------------------------------------------------------
-- Company: 
-- Engineer: 
-- 
-- Create Date:    08:11:09 03/23/2018 
-- Design Name: 
-- Module Name:    er_1octet - Behavioral 
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

entity er_1octet is
    Port ( en : in  STD_LOGIC;
           din : in  STD_LOGIC_VECTOR (7 downto 0);
           dout : out  STD_LOGIC_VECTOR (7 downto 0);
           busy : out  STD_LOGIC;
           clk : in  STD_LOGIC;
           reset : in  STD_LOGIC;
           sclk : out  STD_LOGIC;
           mosi : out  STD_LOGIC;
           miso : in  STD_LOGIC
			  );
end er_1octet;

architecture Behavioral of er_1octet is
	signal in_mem : STD_LOGIC_VECTOR(7 downto 0); 
	
	type erStates is (
		erRAZ, WRITEBIT, READBIT
	);
begin
	p_master : process (clk,reset)
		variable state : erStates;          -- etat de l'automate
		variable cpt_transmission: natural; -- index du bit en cours de transmission
	begin
		if (reset = '1') then
			-- reset
			state := erRAZ;
			dout <= (others => '0');
			busy <= '0';
			sclk <= '0';
			mosi <= '0';
			cpt_transmission := 0;
			in_mem <= (others => '0');
		elsif (rising_edge(clk)) then
			case state is
				when erRAZ =>
					sclk <= '0';
					if (en = '1') then
						busy <= '1';
						-- mémoriser din pour être sûr qu'elle ne change pas
						in_mem <= din;
						dout <= (others => '0');
						-- placer le 1er bit
						cpt_transmission := 0;
						mosi <= din(7);
						state := READBIT;
					end if;
				when WRITEBIT =>
					-- placer un bit a envoyer
					sclk <= '0';
					mosi <= in_mem(7-cpt_transmission);
					state := READBIT;
				when READBIT =>
					-- echanger avec l'esclave
					cpt_transmission := cpt_transmission +1; 
					sclk <= '1';
					dout(8-cpt_transmission) <= miso;
					if cpt_transmission < 8 then
						state := WRITEBIT;
					else
						-- end
						busy <= '0';
						state := erRAZ;
						cpt_transmission := 0;
					end if;
			end case;
		end if;
	end process;
end Behavioral;

