package com.wcs.common.filenet.pe.exception;


import filenet.vw.api.VWException;

public class InvalidColumnException extends P8BpmException {

    public InvalidColumnException(VWException cause) {
        super(cause);
    }

}
