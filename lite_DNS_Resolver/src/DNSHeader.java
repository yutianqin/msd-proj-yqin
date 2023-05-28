import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

// 4.1.1. Header section format
// The header contains the following fields:
//
//        1  1  1  1  1  1
//        0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                      ID                       |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    QDCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    ANCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    NSCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//        |                    ARCOUNT                    |
//        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
//
//        where:
//
//        ID              A 16 bit identifier assigned by the program that
//        generates any kind of query.  This identifier is copied
//        the corresponding reply and can be used by the requester
//        to match up replies to outstanding queries.
//
//        QR              A one bit field that specifies whether this message is a
//        query (0), or a response (1).
//
//        OPCODE          A four bit field that specifies kind of query in this
//        message.  This value is set by the originator of a query
//        and copied into the response.  The values are:
//
//        0               a standard query (QUERY)
//
//        1               an inverse query (IQUERY)
//
//        2               a server status request (STATUS)
//
//        3-15            reserved for future use
//
//        AA              Authoritative Answer - this bit is valid in responses,
//        and specifies that the responding name server is an
//        authority for the domain name in question section.
//
//        Note that the contents of the answer section may have
//        multiple owner names because of aliases. The AA bit
//        corresponds to the name which matches the query name, or
//        the first owner name in the answer section.
//
//        TC              TrunCation - specifies that this message was truncated
//        due to length greater than that permitted on the
//        transmission channel.
//
//        RD              Recursion Desired - this bit may be set in a query and
//        is copied into the response.  If RD is set, it directs
//        the name server to pursue the query recursively.
//        Recursive query support is optional.
//
//        RA              Recursion Available - this be is set or cleared in a
//        response, and denotes whether recursive query support is
//        available in the name server.
//
//        Z               Reserved for future use.  Must be zero in all queries
//        and responses.
//
//        RCODE           Response code - this 4 bit field is set as part of
//        responses.  The values have the following
//        interpretation:
//
//        0               No error condition
//
//        1               Format error - The name server was
//        unable to interpret the query.
//
//        2               Server failure - The name server was
//        unable to process this query due to a
//        problem with the name server.
//
//        3               Name Error - Meaningful only for
//        responses from an authoritative name
//        server, this code signifies that the
//        domain name referenced in the query does
//        not exist.
//
//        4               Not Implemented - The name server does
//        not support the requested kind of query.
//
//        5               Refused - The name server refuses to
//        perform the specified operation for
//        policy reasons.  For example, a name
//        server may not wish to provide the
//        information to the particular requester,
//        or a name server may not wish to perform
//        a particular operation (e.g., zone
//        transfer) for particular data.
//
//        6-15            Reserved for future use.
//
//        QDCOUNT         an unsigned 16 bit integer specifying the number of
//        entries in the question section.
//
//        ANCOUNT         an unsigned 16 bit integer specifying the number of
//        resource records in the answer section.
//
//        NSCOUNT         an unsigned 16 bit integer specifying the number of name
//        server resource records in the authority records
//        section.
//
//        ARCOUNT         an unsigned 16 bit integer specifying the number of
//        resource records in the additional records section.



/**
 * This class stores all the data provided by the 12 byte DNS header
 */
public class DNSHeader {

    private boolean isAnswer = false;
    private int ID = 0;
    private int FLAGS = 0;
    private int QDCOUNT = 0;
    private int ANCOUNT = 0;
    private int NSCOUNT = 0;
    private int ARCOUNT = 0;

    public boolean isAnswer() {
        return isAnswer;
    }


    public int getID() {
        return ID;
    }

    public int getFLAGS() {
        return FLAGS;
    }

    public int getQDCOUNT() {
        return QDCOUNT;
    }

    public int getANCOUNT() {
        return ANCOUNT;
    }

    public int getNSCOUNT() {
        return NSCOUNT;
    }

    public int getARCOUNT() {
        return ARCOUNT;
    }



    /**
     * Read the header from an input stream
     */
    static DNSHeader decodeHeader( InputStream inputStream ) {
        try ( DataInputStream input = new DataInputStream( inputStream ) )
        {
            DNSHeader header = new DNSHeader();

            System.out.println( "HEADER" );

            header.ID = input.readUnsignedShort();
            System.out.print( "\tID: " + header.ID  );

            header.FLAGS = input.readUnsignedShort();
            if ( ( header.FLAGS & 0x8000 ) == 0x8000 ) header.isAnswer = true;
            System.out.print( "\tFLAGS: " + header.FLAGS );

            header.QDCOUNT = input.readUnsignedShort();
            System.out.print( "\tQDCOUNT: " + header.QDCOUNT );

            header.ANCOUNT = input.readUnsignedShort();
            System.out.print( "\tANCOUNT: " + header.ANCOUNT );

            header.NSCOUNT = input.readUnsignedShort();
            System.out.print( "\tNSCOUNT: " + header.NSCOUNT );

            header.ARCOUNT = input.readUnsignedShort();
            System.out.print( "\tARCOUNT: " + header.ARCOUNT + "\n" );

            return header;
        }
        catch ( IOException e )
        {
            System.out.println( "Ahh error occurs!!" );
            e.printStackTrace();
            return null;
        }
    }



    /**
     * Create the header for the response. It will copy some fields from the request
     */
    static DNSHeader buildHeaderForResponse( DNSMessage request, DNSMessage response ) {

        DNSHeader tempHeader = new DNSHeader();

        tempHeader.ID = request.header.getID();
        tempHeader.FLAGS = request.header.FLAGS | ( 1<<15 ); // flip the 1st bit from 0 to 1
        tempHeader.QDCOUNT = request.header.QDCOUNT;
        tempHeader.ANCOUNT = request.records.length; // get the number of answers
        tempHeader.NSCOUNT = 0;
        tempHeader.ARCOUNT = request.header.ARCOUNT;

        response.header = tempHeader;

        return tempHeader;
    }



    /**
     * Encode the header to bytes to be sent back to the client
     * Assuming a 16 bit wide table, the layout is essentially:
     * [byte 0 bit 7, byte 0 bit 6, byte 0, bit 5, ..., byte 0 bit 0, byte 1 bit 7, byte 1 bit 6, ..., byte 1 bit 0]
     */
    void writeBytes( OutputStream outputStream ) {

        // Write all the fields of this DNSHeader into ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate( 12 ); // 12 bytes info
        buffer.put( (byte) ( (ID >> 8) & 0xFF ) );
        buffer.put ( (byte) ( ID & 0xFF ) );

        buffer.put( (byte) ( (FLAGS >> 8) & 0xFF ) );
        buffer.put ( (byte) ( FLAGS & 0xFF ) );

        buffer.put( (byte) ( (QDCOUNT >> 8) & 0xFF ) );
        buffer.put ( (byte) ( QDCOUNT & 0xFF ) );

        buffer.put( (byte) ( (ANCOUNT >> 8) & 0xFF ) );
        buffer.put ( (byte) ( ANCOUNT & 0xFF ) );

        buffer.put( (byte) ( (NSCOUNT >> 8) & 0xFF ) );
        buffer.put ( (byte) ( NSCOUNT & 0xFF ) );

        buffer.put( (byte) ( (ARCOUNT >> 8) & 0xFF ) );
        buffer.put ( (byte) ( ARCOUNT & 0xFF ) );

        // Write ByteBuffer to the OutputStream by using WritableByteChannel adapter
        try ( WritableByteChannel channel = Channels.newChannel( outputStream ) )
        {
            channel.write( buffer );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}
