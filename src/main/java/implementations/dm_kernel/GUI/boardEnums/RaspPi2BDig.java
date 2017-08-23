package implementations.dm_kernel.GUI.boardEnums;

public enum RaspPi2BDig{
	PIN_1(1),
    PIN_2(2),
    PIN_3(3),
    PIN_4(4),
    PIN_5(5),
    PIN_6(6),
    PIN_7(7),
    PIN_8(8),
    PIN_9(9),
    PIN_10(10),
    PIN_11(11),
    PIN_12(12),
    PIN_13(13),
    PIN_14(14),
    PIN_15(15),
    PIN_16(16),
    PIN_17(17),
    PIN_18(18),
    PIN_19(19),
    PIN_20(20),
    PIN_21(21),
    PIN_22(22),
    PIN_23(23),
    PIN_24(24),
    PIN_25(25),
    PIN_26(26),
    PIN_27(27),
    PIN_28(28),
    PIN_29(29),
    PIN_30(30),
    PIN_31(31),
    PIN_32(32),
    PIN_33(33),
    PIN_34(34),
    PIN_35(35),
    PIN_36(36),
    PIN_37(37),
    PIN_38(38),
    PIN_39(39),
    PIN_40(40),
    PIN_41(41); // RaspCam
	
    private final int value;

    private RaspPi2BDig(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
    
    public int getItemCount(){
        return RaspPi2BDig.values().length - 1;
    }
}
