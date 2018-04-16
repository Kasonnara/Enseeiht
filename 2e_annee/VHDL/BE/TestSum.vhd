--------------------------------------------------------------------------------
-- Company: 
-- Engineer:
--
-- Create Date:   10:43:39 03/30/2018
-- Design Name:   
-- Module Name:   /mnt/nosave/sdeneuvi/xilinx//TestSum.vhd
-- Project Name:  projet
-- Target Device:  
-- Tool versions:  
-- Description:   
-- 
-- VHDL Test Bench Created by ISE for module: MasterSum
-- 
-- Dependencies:
-- 
-- Revision:
-- Revision 0.01 - File Created
-- Additional Comments:
--
-- Notes: 
-- This testbench has been automatically generated using types std_logic and
-- std_logic_vector for the ports of the unit under test.  Xilinx recommends
-- that these types always be used for the top-level I/O of a design in order
-- to guarantee that the testbench will bind correctly to the post-implementation 
-- simulation model.
--------------------------------------------------------------------------------
LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
-- Uncomment the following library declaration if using
-- arithmetic functions with Signed or Unsigned values
--USE ieee.numeric_std.ALL;
 
ENTITY TestSum IS
END TestSum;
 
ARCHITECTURE behavior OF TestSum IS 
 
    -- Component Declaration for the Unit Under Test (UUT)
 
    COMPONENT MasterSum
    PORT(
         en : IN  std_logic;
         e1 : IN  std_logic_vector(7 downto 0);
         e2 : IN  std_logic_vector(7 downto 0);
         s : OUT  std_logic_vector(7 downto 0);
         carry : OUT  std_logic;
         busy : OUT  std_logic;
         mosi : OUT  std_logic;
         miso : IN  std_logic;
         ss : OUT  std_logic;
         sclk : OUT  std_logic;
         clk : IN  std_logic;
         reset : IN  std_logic
        );
    END COMPONENT;
	 
	COMPONENT slave_sum
	PORT(
		sclk : IN std_logic;
		mosi : IN std_logic;
		ss : IN std_logic;          
		miso : OUT std_logic
		);
	END COMPONENT;
    

   --Inputs
   signal en : std_logic := '0';
   signal e1 : std_logic_vector(7 downto 0) := (others => '0');
   signal e2 : std_logic_vector(7 downto 0) := (others => '0');
   signal miso : std_logic := '0';
   signal clk : std_logic := '0';
   signal reset : std_logic := '0';

 	--Outputs
   signal s : std_logic_vector(7 downto 0);
   signal carry : std_logic;
   signal busy : std_logic;
   signal mosi : std_logic;
   signal ss : std_logic;
   signal sclk : std_logic;

   -- Clock period definitions
   constant sclk_period : time := 10 ns;
   constant clk_period : time := 10 ns;
 
BEGIN
 
	-- Instantiate the Unit Under Test (UUT)
   uut: MasterSum PORT MAP (
          en => en,
          e1 => e1,
          e2 => e2,
          s => s,
          carry => carry,
          busy => busy,
          mosi => mosi,
          miso => miso,
          ss => ss,
          sclk => sclk,
          clk => clk,
          reset => reset
        );
		  
	Inst_slave_sum: slave_sum PORT MAP(
		sclk => sclk,
		mosi => mosi,
		miso => miso,
		ss => ss
	);
   -- Clock process definitions
 
   clk_process :process
   begin
		clk <= '0';
		wait for clk_period/2;
		clk <= '1';
		wait for clk_period/2;
   end process;
 

   -- Stimulus process
   stim_proc: process
		variable nb_test: natural;
		variable cpt_limit : natural; -- pour empecher une eventuel boucle infinie
   begin		
      -- hold reset state for 100 ns.
      reset <= '1';
		nb_test := 0;
		cpt_limit := 0;
		wait for 100 ns;	
		reset <= '0';
      wait for sclk_period*10;
		
      -- insert stimulus here 
		while (nb_test < 5 and cpt_limit < 200) loop
			cpt_limit := cpt_limit + 1;
			if (busy = '1') then
				en <= '0';
			else
				-- placer e1 et e2
				case nb_test is
					when 0 => 	e1 <= "00000001"; -- test petit nombre
									e2 <= "00000010"; 
					when 1 => 	e1 <= "01010101"; -- 
									e2 <= "01010101";
					when 2 => 	e1 <= "10000000"; -- test carry 
									e2 <= "10000000";
					when 3 => 	e1 <= "11001100"; -- test sum 0
									e2 <= "00000000";
					when 4 => 	e1 <= "00000000"; -- recupérer la valeur du dernier test
									e2 <= "00000000";
					when others => null;
				end case;
				en <= '1';
				nb_test := nb_test + 1;		
				wait for clk_period*2;
			end if;
			wait for clk_period;
		end loop;
			en <= '0';
      wait;
   end process;

END;
