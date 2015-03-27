module LastFmImporter 

open System
open FSharp.Data
open HttpClient
open Microsoft.FSharp.Control

// Build our types based on samples of the JSON we expect.
type LastFmApiCredentials = JsonProvider<"data/lastfm/credentials.sample.json">
type LastFmTopTracks = JsonProvider<"data/lastfm/user.getTopTracks.json">
type LastFmRecentTracks = JsonProvider<"data/lastfm/user.getRecentTracks.json">
type LastFmUser = JsonProvider<"data/lastfm/user.getInfo.json">

// You need to provide this file with a valid API key for this to work
let loadApiKey () =
    LastFmApiCredentials.Load(
        __SOURCE_DIRECTORY__ + "/data/lastfm/credentials.json").ApiKey

type User(name: string, trackCount: int) =
    member m.Name = name
    member m.TrackCount = trackCount

type Artist(name: string,
            mbid: Guid option) =
    member m.Name = name
    member m.Mbid = mbid

type Track(name: string,
           artist: Artist,
           mbid: Guid option) =
    member m.Name = name
    member m.Artist = artist
    member m.Mbid = mbid

// Helper for building requests:
type RequestBuilder(username: string, apiKey: string) =
    let baseUri = "http://ws.audioscrobbler.com/2.0/"

    let baseRequest =
        createRequest Get baseUri
        |> withQueryStringItem { name="api_key"; value=apiKey }  
        |> withQueryStringItem { name="format"; value="json" }  
        |> withQueryStringItem { name="user"; value=username }

    let forMethod (methodName: string):Request =
        baseRequest 
        |> withQueryStringItem { name="method"; value=methodName }  

    member m.forUserInfo = forMethod "user.getInfo"
    member m.forTopTracks = forMethod "user.getTopTracks"
    member m.forRecentTracks = forMethod "user.getRecentTracks"

// Gets some user information from the Last.fm API
let getUser(username: string):Async<User> = async {
    let apiKey = loadApiKey ()
    let request = RequestBuilder(username, apiKey).forUserInfo
    let! response = (request |> getResponseBodyAsync)
    let json = LastFmUser.Parse(response)
    return User(json.User.Name, json.User.Playcount)
}

// Gets the user's top tracks
let getTopTracksForUser(username: String) = async {
    let apiKey = loadApiKey ()
    let request = RequestBuilder(username, apiKey).forTopTracks
    let! response = (request |> getResponseBodyAsync)
    let tracks = LastFmTopTracks.Parse(response)
    return seq {
        for track in tracks.Toptracks.Track do
            let artist = new Artist(track.Artist.Name, track.Artist.Mbid)
            yield new Track(track.Name, artist, track.Mbid)
    }
}

// Gets all the user's recent tracks
let getRecentTracksForUser(username: String) = async {
    let apiKey = loadApiKey ()
    let request = RequestBuilder(username, apiKey).forRecentTracks
    let! response = (request |> getResponseBodyAsync)
    let tracks = LastFmRecentTracks.Parse(response)
    return seq {
        for track in tracks.Recenttracks.Track do
            let artist = new Artist(track.Artist.Text, track.Artist.Mbid)
            yield new Track(track.Name, artist, track.Mbid)
    }
}
